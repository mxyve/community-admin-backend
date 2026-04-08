package top.xym.web.rag.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.document.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDetailVO {
    private Long id;
    private String fileName;
    private String url;
    private String vectorId;
    private Integer status;
    private LocalDate createTime;
    private LocalDate updateTime;

    // 分词列表（来自向量库）
    private List<Document> splitDocuments;
}