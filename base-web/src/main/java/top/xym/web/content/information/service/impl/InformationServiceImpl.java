package top.xym.web.content.information.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import top.xym.web.content.information.entity.Information;
import top.xym.web.content.information.mapper.InformationMapper;
import top.xym.web.content.information.service.InformationService;

@Service
@AllArgsConstructor
public class InformationServiceImpl extends ServiceImpl<InformationMapper, Information>
        implements InformationService {

    /**
     * 分页查询资讯（支持标题模糊搜索）
     */
    @Override
    public IPage<Information> pageList(Integer pageNum, Integer pageSize, String title) {
        LambdaQueryWrapper<Information> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(Information::getTitle, title);
        }
        wrapper.orderByAsc(Information::getSort)
                .orderByDesc(Information::getCreateTime);

        Page<Information> page = new Page<>(pageNum, pageSize);
        return this.page(page, wrapper);
    }

    /**
     * 修改状态（启用/禁用）
     */
    @Override
    public void updateStatus(Integer id, Integer status) {
        Information information = new Information();
        information.setId(id);
        information.setStatus(status);
        this.updateById(information);
    }
}
