package top.xym.web.admin.merchant_manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import top.xym.web.admin.merchant_manage.entity.PayMerchantOrder;

public interface PayMerchantOrderMapper extends BaseMapper<PayMerchantOrder> {

    @Update("UPDATE pay_merchant_order SET pay_status = 1, pay_time = NOW() WHERE order_no = #{orderNo}")
    int updatePayStatus(@Param("orderNo") String orderNo);

    @Select("SELECT pay_status FROM pay_merchant_order WHERE order_no = #{orderNo}")
    Integer getPayStatus(@Param("orderNo") String orderNo);
}