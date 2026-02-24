package top.xym.result;

public interface StatusCode {
    // 返回成功
    int SUCCESS_CODE = 200;
    // 错误状态码
    int ERROR_CODE = 500;
    // 无权限
    int NO_LOGIN = 600;
    int NO_AUTH = 700;
}
