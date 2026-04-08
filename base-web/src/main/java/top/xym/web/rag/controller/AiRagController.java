package top.xym.web.rag.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import top.xym.web.rag.service.AliOssFileService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rag/chat")
@Slf4j
@Tag(name = "AiRagController", description = "Rag对话接口")
public class AiRagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final AliOssFileService aliOssFileService;

    private static final double SIMILARITY_THRESHOLD = 0.4;
    private static final int TOP_K = 5;
    private static final int MAX_DOC_LENGTH = 1500;

    public AiRagController(ChatModel chatModel, ChatMemory chatMemory, VectorStore vectorStore
    , AliOssFileService aliOssFileService) {
        this.vectorStore = vectorStore;
        this.aliOssFileService = aliOssFileService;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem("""
                        你是社区服务平台的AI助手。
                        今天的日期：{current_date}
                        """)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    @Operation(summary = "rag", description = "Rag对话接口")
    @GetMapping(value = "/rag")
    public Flux<String> generate(@RequestParam(value = "message", defaultValue = "你好") String message) {
        String conversationId = "community_ai_default";
        try {
            // 1. 执行向量检索
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(message)
                    .topK(TOP_K)
                    .similarityThreshold(SIMILARITY_THRESHOLD)
                    .build();

            List<Document> docs = vectorStore.similaritySearch(searchRequest);

            // 启用/禁用过滤
            // 获取所有 启用 的 vectorId
            List<String> enabledVectorIds = aliOssFileService.listEnabledVectorIds();

            // 只保留启用的文档片段
            docs = docs.stream()
                    .filter(doc -> enabledVectorIds.contains(doc.getId()))
                    .toList();

            log.info("向量检索返回 {} 条结果，查询词: {}", docs.size(), message);

            // 2. 构建增强的用户消息
            String enhancedMessage = buildEnhancedMessage(message, docs);

            // 3. 调用 AI
            return chatClient.prompt()
                    .user(enhancedMessage)
                    .advisors(a -> a.param("current_date", LocalDate.now().toString()))
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .stream()
                    .content()
                    .onErrorResume(e -> {
                        log.error("AI 响应出错", e);
                        return Flux.just("抱歉，服务暂时不可用，请稍后再试。");
                    });

        } catch (Exception e) {
            log.error("RAG 处理出错", e);
            return Flux.just("系统繁忙，请稍后再试。");
        }
    }

    private String buildEnhancedMessage(String userQuestion, List<Document> docs) {
        StringBuilder sb = new StringBuilder();

        if (!docs.isEmpty()) {
            sb.append("【参考资料】\n");
            sb.append("请基于以下文档内容回答用户问题：\n\n");

            for (int i = 0; i < docs.size(); i++) {
                String content = cleanDocumentContent(docs.get(i).getText());
                sb.append(i + 1).append(". ").append(content).append("\n\n");
            }

            sb.append("---\n");
            sb.append("要求：\n");
            sb.append("1. 只使用上述文档中的信息回答\n");
            sb.append("2. 如果文档中没有相关信息，明确告知用户\n");
            sb.append("3. 回答要简洁、准确\n\n");
        } else {
            sb.append("（知识库中暂无相关信息）\n\n");
        }

        sb.append("用户问题：").append(userQuestion);
        return sb.toString();
    }

    private String cleanDocumentContent(String rawContent) {
        // 移除页码标记
        String cleaned = rawContent.replaceAll("===== Page \\d+ =====", "").trim();

        // 限制单个文档片段的长度
        if (cleaned.length() > MAX_DOC_LENGTH) {
            cleaned = cleaned.substring(0, MAX_DOC_LENGTH) + "...";
        }

        return cleaned;
    }

    @GetMapping("/test/vector-search")
    public String testVectorSearch(@RequestParam String query) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(TOP_K)
                .similarityThreshold(0.3d)
                .build();

        List<Document> results = vectorStore.similaritySearch(request);

        if (results.isEmpty()) {
            return "向量数据库中没有找到相关文档！查询词：" + query;
        }

        StringBuilder sb = new StringBuilder("找到 " + results.size() + " 条结果：\n");
        for (int i = 0; i < results.size(); i++) {
            String content = results.get(i).getText();
            if (content.length() > 300) {
                content = content.substring(0, 300) + "...";
            }
            sb.append(i + 1).append(". ").append(content).append("\n");
            sb.append("   相似度分数: ").append(results.get(i).getMetadata().get("distance")).append("\n\n");
        }
        return sb.toString();
    }
}