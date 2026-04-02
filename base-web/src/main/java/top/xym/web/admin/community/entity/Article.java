package top.xym.web.admin.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_article")
public class Article {

    @TableId(value = "article_id", type = IdType.AUTO)
    private Integer articleId;

    private Integer userId;

    private Integer tagId;

    private String title;

    private String content;

    private String img;

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