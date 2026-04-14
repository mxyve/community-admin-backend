package top.xym.web.shop.conversation.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "消息发送请求参数")
public class MessageSendRequest {

    @Schema(description = "会话ID（后台商家发送必须传）")
    private Long sessionId;

    @Schema(description = "接收者ID（前台用户首次发送传商家ID）")
    private Long receiverId;

    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型：0文本 1图片 2语音 3视频")
    private Integer msgType;
}