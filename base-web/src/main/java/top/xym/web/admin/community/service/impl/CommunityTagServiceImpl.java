package top.xym.web.admin.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.xym.web.admin.community.entity.Article;
import top.xym.web.admin.community.entity.Tag;
import top.xym.web.admin.community.mapper.ArticleMapper;
import top.xym.web.admin.community.mapper.CommunityTagMapper;
import top.xym.web.admin.community.service.CommunityTagService;

@Service
@AllArgsConstructor
public class CommunityTagServiceImpl
        extends ServiceImpl<CommunityTagMapper, Tag>
        implements CommunityTagService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public long countByTagId(Integer tagId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getTagId, tagId)
                .eq(Article::getDeleted, 0);

        return articleMapper.selectCount(wrapper);
    }

}
