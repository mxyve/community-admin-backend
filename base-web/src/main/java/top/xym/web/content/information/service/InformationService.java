package top.xym.web.content.information.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.web.content.information.entity.Information;

public interface InformationService extends IService<Information> {

    /**
     * 分页查询资讯列表
     */
    IPage<Information> pageList(Integer pageNum, Integer pageSize, String title);

    /**
     * 启用/禁用资讯
     */
    void updateStatus(Integer id, Integer status);
}
