package top.xym.web.admin.community.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


import top.xym.result.Result;
import top.xym.web.admin.community.entity.Article;
import top.xym.web.admin.community.entity.ArticleVO;
import top.xym.web.admin.community.service.ArticleService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/community/article")
@AllArgsConstructor
@Tag(name = "邻里圈文章管理模块")
public class ArticleController {


    private final ArticleService articleService;

    /*
     * 分页查询文章列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param name 分类名称（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询文章列表")
    public Result<IPage<ArticleVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String area
    ) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();


        // 标题
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Article::getTitle, name);
        }

        // 按状态查询
        if (status != null) {
            wrapper.eq(Article::getStatus, status);
        }

        // 地区
        if (area != null && !area.trim().isEmpty()) {
            wrapper.and(w -> w
                    .apply("a.province like concat('%', {0}, '%')", area)
                    .or()
                    .apply("a.city like concat('%', {0}, '%')", area)
                    .or()
                    .apply("a.area like concat('%', {0}, '%')", area)
            );
        }

        IPage<ArticleVO> result = articleService.getArticlePage(page, wrapper);
        return Result.success(result);

    }

    /**
     * 获取全部文章列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取全部文章列表")
    public Result<List<ArticleVO>> list(
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Article::getStatus, status);
        }

        List<ArticleVO> list = articleService.getArticleList(wrapper);
        return Result.success(list);
    }


    /**
     * 新增文章
     */
    @PostMapping("/add")
    @Operation(summary = "新增文章")
    public Result<?> add(@RequestBody Article article) {
        // 检查名称是否已存在
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getTitle, article.getTitle())
                .eq(Article::getDeleted, 0);
        long count = articleService.count(wrapper);
        if (count > 0) {
            return Result.error("分类名称已存在");
        }

        // 设置默认值
        article.setStatus(article.getStatus() != null ? article.getStatus() : 1);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        article.setDeleted(0);

        boolean saved = articleService.save(article);
        return saved ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 修改文章
     */
    @PutMapping("/update")
    @Operation(summary = "修改文章")
    public Result<?> update(@RequestBody Article article) {
        if (article.getArticleId() == null) {
            return Result.error("文章ID不能为空");
        }

        // 检查名称是否已存在
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getTitle, article.getTitle())
                .eq(Article::getDeleted, 0)
                .ne(Article::getArticleId, article.getArticleId());
        long count = articleService.count(wrapper);
        if (count > 0) {
            return Result.error("分类名称已存在");
        }

        article.setUpdateTime(LocalDateTime.now());
        boolean updated = articleService.updateById(article);
        return updated ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除文章")
    public Result<?> delete(@PathVariable Integer id) {
        Article article = new Article();
        article.setArticleId(id);
        article.setDeleted(1);
        return articleService.updateById(article) ? Result.success("删除成功") : Result.error("失败");
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "获取文章详情")
    public Result<ArticleVO> detail(@PathVariable Integer id) {
        ArticleVO article = articleService.getArticleDetail(id);
        return Result.success(article);
    }

    /**
     * 审核通过
     */
    @PutMapping("/auditPass/{id}")
    @Operation(summary = "审核通过")
    public Result<?> auditPass(@PathVariable Integer id) {
        Article article = new Article();
        article.setArticleId(id);
        article.setStatus(1); // 1=审核通过
        article.setUpdateTime(LocalDateTime.now());
        boolean updated = articleService.updateById(article);
        return updated ? Result.success("审核成功") : Result.error("审核失败");
    }

    /**
     * 审核不通过
     */
    @PutMapping("/auditReject/{id}")
    @Operation(summary = "审核不通过")
    public Result<?> auditReject(@PathVariable Integer id) {
        Article article = new Article();
        article.setArticleId(id);
        article.setStatus(0); // 0=审核不通过
        article.setUpdateTime(LocalDateTime.now());
        boolean updated = articleService.updateById(article);
        return updated ? Result.success("已驳回") : Result.error("驳回失败");
    }


}
