package top.xym.web.admin.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.xym.web.admin.community.entity.Article;
import top.xym.web.admin.community.entity.ArticleVO;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    IPage<ArticleVO> selectArticlePage(Page<ArticleVO> page, @Param("ew") com.baomidou.mybatisplus.core.conditions.Wrapper<Article> wrapper);

    // 查询文章列表（带用户名）
    List<ArticleVO> selectArticleList(@Param("ew") com.baomidou.mybatisplus.core.conditions.Wrapper<Article> wrapper);

    // 查询文章详情（带用户名）
    ArticleVO selectArticleDetail(@Param("articleId") Integer articleId);

}
