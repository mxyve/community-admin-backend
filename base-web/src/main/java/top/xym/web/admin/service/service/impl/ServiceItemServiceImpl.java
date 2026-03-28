package top.xym.web.admin.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xym.web.admin.service.entity.Services;
import top.xym.web.admin.service.mapper.ServiceItemMapper;
import top.xym.web.admin.service.service.ServiceItemService;

@Service
public class ServiceItemServiceImpl
        extends ServiceImpl<ServiceItemMapper, Services>
        implements ServiceItemService {

    @Override
    public long countByCategoryId(Integer categoryId) {
        LambdaQueryWrapper<Services> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Services::getCategoryId, categoryId)
                .eq(Services::getDeleted, 0);
        return this.count(wrapper);
    }
}