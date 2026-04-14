package top.xym.web.shop.conversation.controller;

import com.alibaba.fastjson2.JSON;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xym.web.shop.conversation.config.AdminWebSocket;
import top.xym.result.Result;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatPushController {

    @PostMapping("/push-to-merchant")
    public Result<?> pushToMerchant(@RequestBody Map<String, Object> map) {
        Long merchantId = Long.valueOf(map.get("merchantId").toString());
        Object msg = map.get("message");

        AdminWebSocket.sendToMerchant(merchantId, JSON.toJSONString(msg));
        return Result.success();
    }
}
