package top.xym.web.shop.conversation.service;

import top.xym.web.shop.conversation.model.dto.MessageSendRequest;
import top.xym.web.shop.conversation.model.entity.ConversationMessage;

import java.util.List;

public interface AdminConversationMessageService {
    List<ConversationMessage> getMessageList(Integer merchantId, Long sessionId);
    void sendMessage(Integer merchantId, MessageSendRequest request);
    void markRead(Integer merchantId, Long sessionId);
}