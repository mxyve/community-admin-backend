package top.xym.web.admin.community.service;


import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.web.admin.community.entity.Tag;


public interface CommunityTagService extends IService<Tag> {

    /**
     * 删除分类Id统计文章数量
     */
    long countByTagId(Integer tagId);
}
