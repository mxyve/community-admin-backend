package top.xym.web.shop.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import top.xym.web.shop.service.entity.ServiceCategory;
import top.xym.web.shop.service.mapper.ServiceCategoryMapper;
import top.xym.web.shop.service.service.ServiceCategoryService;

import java.util.List;

@Service
@AllArgsConstructor
public class ServiceCategoryServiceImpl extends ServiceImpl<ServiceCategoryMapper, ServiceCategory> implements ServiceCategoryService {

    @Override
    public List<ServiceCategory> getAllAvailableCategories() {
        QueryWrapper<ServiceCategory> query = new QueryWrapper<>();
        // 只查询显示状态的分类
        query.lambda().eq(ServiceCategory::getStatus, 1)
                .orderByAsc(ServiceCategory::getSort);
        return this.baseMapper.selectList(query);
    }
}