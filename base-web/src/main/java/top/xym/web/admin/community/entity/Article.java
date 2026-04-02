package top.xym.web.admin.community.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_article")
public class Article {

    @TableId(type = IdType.AUTO)
    private Integer articleId;

    private Long userId;

    private Integer tagId;

    private String title;

    private String content;

    private String img;

    // 0：审核不通过 1：审核通过 2：审核中
    private Integer status;

    private Long viewCount;

    private Long likeCount;

    private Long commentCount;

    private String province;

    private String city;

    private String area;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer deleted;

}