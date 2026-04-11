package top.xym.web.rag.controller;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.xym.result.Result;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.web.rag.entity.AliOssFile;
import top.xym.web.rag.entity.vo.KnowledgeDetailVO;
import top.xym.web.rag.service.AliOssFileService;
import top.xym.web.rag.utils.AliOssUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.alibaba.fastjson2.JSON.parseArray;

@RestController
@RequestMapping("/api/rag")
@AllArgsConstructor
@Slf4j
@Tag(name = "RAG知识库管理模块")
public class KnowledgeController {

    private VectorStore vectorStore;

    private AliOssUtil aliOssUtil;

    private final TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

    private AliOssFileService aliOssFileService;

    @Operation(summary = "upload", description = "上传附件")
    @PostMapping(value = "file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultVo<Object> upload(@RequestParam("file") MultipartFile[] files) {
        if (files.length == 0) {
            return ResultUtils.error("文件不能为空");
        }

        // 上传文件
        for (MultipartFile file : files) {
            // 上传OSS

            try {
                // 原文件名
                String originalFilename = file.getOriginalFilename();
                // 文件后缀
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                // 随机文件名（OSS）
                String objectName = UUID.randomUUID() + extension;
                String url = aliOssUtil.upload(file.getBytes(), objectName);

                // 向量化
                // 1、读取文件
                Resource resource = file.getResource();
                TikaDocumentReader reader = new TikaDocumentReader(resource);
                List<Document> documents = reader.read();
                // 2、分词
                List<Document> apply = tokenTextSplitter.apply(documents);
                // 3、向量化
                // 4、保存向量 自动调用向量模型向量化方法
                vectorStore.add(apply);

                // 5、持久化到数据库
                long currMillis = System.currentTimeMillis();
                aliOssFileService.save(AliOssFile.builder()
                        .fileName(originalFilename)
                        .vectorId(JSON.toJSONString(apply.stream().map(Document::getId).collect(Collectors.toList())))
                        .url(url)
                        .createTime(LocalDate.now())
                        .updateTime(LocalDate.now())
                        .build());
            } catch (IOException e) {
                log.error("上传失败", e);
                return ResultUtils.error("上传文件失败");
            } catch (Exception e) {
                log.error("上传失败", e);
                return ResultUtils.error("向量化失败");
            }
        }
        return ResultUtils.success("文件上传成功");
    }

    @Operation(summary = "分页查询知识库列表")
    @GetMapping("/file/page")
    public Result<IPage<AliOssFile>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) Integer status) {
        Page<AliOssFile> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AliOssFile> wrapper = new LambdaQueryWrapper<>();

        // 按问文件名模糊查询
        if (fileName != null && !fileName.trim().isEmpty()) {
            wrapper.like(AliOssFile::getFileName, fileName);
        }

        // 按状态查询
        if (status != null) {
            wrapper.eq(AliOssFile::getStatus, status);
        }

        // 按创建时间倒序
        wrapper.orderByDesc(AliOssFile::getCreateTime);

        IPage<AliOssFile> result = aliOssFileService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 修改 RAG 知识库文章状态（启用/禁用）
     * @param id 知识库文件ID
     * @param status 0=启用，1=禁用
     * @return 操作结果
     */
    @PutMapping("/file/status/{id}")
    @Operation(summary = "修改RAG知识库文章状态")
    public Result<String> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {

        // 校验状态
        if (status == null || (!status.equals(0) && !status.equals(1))) {
            return Result.error("状态只能是0（启用）或1（禁用）");
        }

        AliOssFile aliOssFile = new AliOssFile();
        aliOssFile.setId(id);
        aliOssFile.setStatus(status);
        aliOssFile.setUpdateTime(LocalDate.now());

        aliOssFileService.updateById(aliOssFile);
        return Result.success("状态修改成功");
    }

    /**
     * 查询RAG知识库文章详情（含向量分词内容）
     * @param id 知识库文件ID
     * @return 详情+分词列表
     */
    @GetMapping("/file/{id}")
    @Operation(summary = "查询RAG知识库文章详情（含分词）")
    public Result<KnowledgeDetailVO> getDetail(@PathVariable Long id) {

        // 查询文章信息
        AliOssFile file = aliOssFileService.getById(id);
        if (file == null) {
            return Result.error("文章不存在");
        }

        // 解析向量ID
        List<String> vectorIdList = parseArray(file.getVectorId(), String.class);

        // 从向量库查询对应分词文档
        List<Document> documentList = vectorStore.similaritySearch(
                SearchRequest.builder().query("").topK(vectorIdList.size()).build()
        );

        // 封装返回
        KnowledgeDetailVO vo = KnowledgeDetailVO.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .url(file.getUrl())
                .status(file.getStatus())
                .createTime(file.getCreateTime())
                .updateTime(file.getUpdateTime())
                .splitDocuments(documentList)
                .build();

        return Result.success(vo);
    }

    @Operation(summary = "delete", description = "文件删除")
    @PostMapping("/file/delete")
    public ResultVo<?> deleteFiles(@RequestParam List<Long> ids) {
        return aliOssFileService.deleteFiles(ids);
    }

}
