package top.xym.web.shop.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("service_staff")
public class ServiceStaff {

    @TableId(type = IdType.AUTO)
    private Integer id;
    // 商家ID（关联 service_merchant 表）
    private Integer merchantId;
    // 服务人员姓名
    private String name;
    // 手机号
    private String phone;
    // 头像
    private String avatar;
    // 性别 0-未知 1-男 2-女
    private Integer sex;
    // 服务分类ID集合，逗号分隔
    @TableField(updateStrategy  = FieldStrategy.ALWAYS)  // 强制更新，即使是空字符串
    private String categoryIds;
    // 状态 0-休息 1-服务中 2-空闲
    private Integer status;
    // 服务区域JSON数组
    @TableField(updateStrategy  = FieldStrategy.ALWAYS)  // 强制更新，即使是空字符串
    private String serviceAreas;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}