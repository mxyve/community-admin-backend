package top.xym.web.rag.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.vector.request.DeleteReq;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.web.rag.entity.AliOssFile;
import top.xym.web.rag.mapper.AliOssFileMapper;
import top.xym.web.rag.service.AliOssFileService;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.fastjson2.JSON.parseArray;

@Service
public class AliOssFileServiceImpl extends ServiceImpl<AliOssFileMapper, AliOssFile>
        implements AliOssFileService {

    @Autowired
    private VectorStore vectorStore;


    @Override
    public List<String> listEnabledVectorIds() {
        // 查询所有 启用状态 的文档
        List<AliOssFile> files = this.lambdaQuery()
                .eq(AliOssFile::getStatus, 0)
                .list();

        // 把所有启用的 vectorId 全部展开成一个 List
        return files.stream()
                .flatMap(file -> parseArray(file.getVectorId(), String.class).stream())
                .toList();
    }

    private MilvusClientV2 initMilvusClient() {
        ConnectConfig config = ConnectConfig.builder()
                .uri("http://localhost:19530")
                .build();
        return new MilvusClientV2(config);
    }


    @Override
    public ResultVo<?> deleteFiles(List<Long> ids) {
        // 查询这批文件里所有的 vectorId
        List<AliOssFile> fileList = this.listByIds(ids);
        for (AliOssFile file : fileList) {
            List<String> vectorIds = JSON.parseArray(file.getVectorId(), String.class);

            // 原生 Milvus SDK 删除
            MilvusClientV2 client = initMilvusClient();
            DeleteReq req = DeleteReq.builder()
                    .collectionName("vector_store")
                    .ids(new ArrayList<>(vectorIds))
                    .build();
            client.delete(req);
        }
        // 删除 mysql
        this.removeByIds(ids);
        return ResultUtils.success("删除成功");
    }

}