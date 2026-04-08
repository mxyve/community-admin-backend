package top.xym.web.rag.entity.dto;

import lombok.Data;

@Data
public class KnowledgePageQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    // 文件名模糊查询
    private String fileName;
    // 状态筛选
    private Integer status;
}