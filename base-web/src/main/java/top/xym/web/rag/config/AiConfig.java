package top.xym.web.rag.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class AiConfig {

    @Bean
    public ChatMemory chatMemory() {
        return new ChatMemory() {

            private final Map<String, List<Message>> memoryMap = new ConcurrentHashMap<>();

            @Override
            public List<Message> get(String conversationId) {
                return memoryMap.getOrDefault(conversationId, List.of());
            }

            @Override
            public void add(String conversationId, List<Message> messages) {
                memoryMap.put(conversationId, messages);
            }

            @Override
            public void clear(String conversationId) {
                memoryMap.remove(conversationId);
            }
        };
    }
}