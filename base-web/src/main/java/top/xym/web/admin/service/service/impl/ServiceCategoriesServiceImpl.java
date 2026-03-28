package top.xym.web.admin.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xym.web.admin.service.entity.ServiceCategory;
import top.xym.web.admin.service.mapper.ServiceCategoriesMapper;
import top.xym.web.admin.service.service.ServiceCategoriesService;

import top.xym.web.shop.service.mapper.ServiceCategoryMapper;

@Service
public class ServiceCategoriesServiceImpl
        extends ServiceImpl<ServiceCategoriesMapper, ServiceCategory>
        implements ServiceCategoriesService {

}
