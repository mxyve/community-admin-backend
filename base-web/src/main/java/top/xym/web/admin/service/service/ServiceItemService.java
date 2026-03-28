package top.xym.web.admin.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.web.admin.service.entity.Services;

public interface ServiceItemService extends IService<Services> {

    /**
     * 根据分类ID统计服务项目数量
     * @param categoryId 分类ID
     * @return 数量
     */
    long countByCategoryId(Integer categoryId);
}