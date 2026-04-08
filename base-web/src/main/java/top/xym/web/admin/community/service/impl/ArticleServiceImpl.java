package top.xym.web.admin.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import top.xym.web.admin.community.entity.Article;
import top.xym.web.admin.community.entity.ArticleVO;
import top.xym.web.admin.community.mapper.ArticleMapper;
import top.xym.web.admin.community.service.ArticleService;

import java.util.List;

@Service
@AllArgsConstructor
public class ArticleServiceImpl
    extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService {
    private final ArticleMapper articleMapper;

    @Override
    public IPage<ArticleVO> getArticlePage(Page<Article> page, Wrapper<Article> wrapper) {
        // 转换分页参数
        Page<ArticleVO> voPage = new Page<>(page.getCurrent(), page.getSize());
        return articleMapper.selectArticlePage(voPage, wrapper);
    }

    @Override
    public List<ArticleVO> getArticleList(Wrapper<Article> wrapper) {
        return articleMapper.selectArticleList(wrapper);
    }

    @Override
    public ArticleVO getArticleDetail(Integer articleId) {
        return articleMapper.selectArticleDetail(articleId);
    }
}
