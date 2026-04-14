package top.xym.web.shop.conversation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import top.xym.web.shop.conversation.model.entity.ConversationSession;

public interface AdminConversationSessionService {
    IPage<ConversationSession> pageList(Integer merchantId, Integer pageNum, Integer pageSize);
}