package top.xym.web.shop.order.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.xym.web.shop.order.entity.ServiceOrder;

@Mapper
public interface ServiceOrderMapper extends BaseMapper<ServiceOrder> {

    IPage<ServiceOrder> selectPageVo(Page<ServiceOrder> page, @Param("ew") QueryWrapper<ServiceOrder> queryWrapper);

}