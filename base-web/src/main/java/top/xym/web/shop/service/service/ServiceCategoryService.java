package top.xym.web.shop.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.web.shop.service.entity.ServiceCategory;

import java.util.List;

public interface ServiceCategoryService extends IService<ServiceCategory> {

    // 查询所有可用分类（status=1，按sort升序）
    List<ServiceCategory> getAllAvailableCategories();
}