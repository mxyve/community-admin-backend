package top.xym.web.sys_user.entity;

import lombok.Data;

@Data
public class LoginVo {
    private Long userId;
    private String nickName;
    private String token;
}
