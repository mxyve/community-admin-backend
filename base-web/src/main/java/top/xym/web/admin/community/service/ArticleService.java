package top.xym.web.admin.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.web.admin.community.entity.Article;
import top.xym.web.admin.community.entity.ArticleVO;

import java.util.List;

public interface ArticleService extends IService<Article> {

    // 分页查询文章列表（带用户名）
    IPage<ArticleVO> getArticlePage(Page<Article> page, com.baomidou.mybatisplus.core.conditions.Wrapper<Article> wrapper);

    // 查询文章列表（带用户名）
    List<ArticleVO> getArticleList(com.baomidou.mybatisplus.core.conditions.Wrapper<Article> wrapper);

    // 查询文章详情（带用户名）
    ArticleVO getArticleDetail(Integer articleId);
}
