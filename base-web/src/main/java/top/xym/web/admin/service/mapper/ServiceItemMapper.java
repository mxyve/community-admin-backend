package top.xym.web.admin.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.xym.web.admin.service.entity.AdminServiceParm;
import top.xym.web.admin.service.entity.Services;

@Mapper
public interface ServiceItemMapper extends BaseMapper<Services> {

    // 超管分页查询（服务 + 商家信息）
    IPage<Services> selectAdminServicePage(IPage<Services> page,
                                           @Param("parm") AdminServiceParm parm);
}
