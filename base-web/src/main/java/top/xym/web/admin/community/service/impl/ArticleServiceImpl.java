package top.xym.web.admin.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import top.xym.web.admin.community.entity.Article;
import top.xym.web.admin.community.mapper.ArticleMapper;
import top.xym.web.admin.community.service.ArticleService;

@Service
@AllArgsConstructor
public class ArticleServiceImpl
    extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService {

}
