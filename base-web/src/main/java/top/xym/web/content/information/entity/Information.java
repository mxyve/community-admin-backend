package top.xym.web.content.information.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("i_information")
public class Information {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 资讯标题
    private String title;

    // 资讯内容
    private String content;

    // 封面图片
    private String coverImg;

    // 服务地区
    private String serviceArea;

    // 排序
    private Integer sort;

    // 状态 0禁用 1启用
    private Integer status;

    // 浏览量
    private Integer viewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}