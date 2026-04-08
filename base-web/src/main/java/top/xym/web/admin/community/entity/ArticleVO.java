package top.xym.web.admin.community.entity;

import lombok.Data;

@Data
public class ArticleVO extends Article {
    private String username;
    private String nickName;
}