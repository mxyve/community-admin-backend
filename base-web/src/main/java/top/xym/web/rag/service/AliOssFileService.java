package top.xym.web.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.result.ResultVo;
import top.xym.web.rag.entity.AliOssFile;

import java.util.List;

public interface AliOssFileService extends IService<AliOssFile> {

    List<String> listEnabledVectorIds();

    ResultVo<?> deleteFiles(List<Long> ids);
}