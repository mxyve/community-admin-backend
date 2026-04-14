package top.xym.web.shop.conversation.config;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/admin/chat/{merchantId}")
public class AdminWebSocket {

    public static final Map<Long, Session> MERCHANT_SESSION_MAP = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("merchantId") Long merchantId) {
        MERCHANT_SESSION_MAP.put(merchantId, session);
        log.info("商家{}连接WebSocket", merchantId);
    }

    @OnClose
    public void onClose(@PathParam("merchantId") Long merchantId) {
        MERCHANT_SESSION_MAP.remove(merchantId);
        log.info("商家{}断开WebSocket", merchantId);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("merchantId") Long merchantId) {}

    @OnError
    public void onError(Throwable error) {
        log.error("后台WebSocket异常", error);
    }

    // ==================== 推送消息给商家自己 ====================
    public static void sendToMerchant(Long merchantId, String message) {
        Session session = MERCHANT_SESSION_MAP.get(merchantId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error("推送商家消息失败", e);
            }
        }
    }
}