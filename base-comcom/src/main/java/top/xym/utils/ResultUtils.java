package top.xym.utils;

import top.xym.result.ResultVo;
import top.xym.result.StatusCode;

public class ResultUtils {

    /**
     * 成功 - 无参数
     */
    public static ResultVo<Object> success() {
        return new ResultVo<>("操作成功", StatusCode.SUCCESS_CODE, null);
    }

    /**
     * 成功 - 只带消息
     */
    public static ResultVo<Object> success(String msg) {
        return new ResultVo<>(msg, StatusCode.SUCCESS_CODE, null);
    }

    /**
     * 成功 - 带消息 + 数据
     */
    public static ResultVo<Object> success(String msg, Object data) {
        return new ResultVo<>(msg, StatusCode.SUCCESS_CODE, data);
    }

    /**
     * 成功 - 自定义状态码
     */
    public static ResultVo<Object> success(String msg, int code, Object data) {
        return new ResultVo<>(msg, code, data);
    }

    // -------------------------------------------------------------------------

    /**
     * 失败 - 默认
     */
    public static ResultVo<Object> error() {
        return new ResultVo<>("操作失败", StatusCode.ERROR_CODE, null);
    }

    /**
     * 失败 - 只带消息
     */
    public static ResultVo<Object> error(String msg) {
        return new ResultVo<>(msg, StatusCode.ERROR_CODE, null);
    }

    /**
     * 失败 - 消息 + 状态码
     */
    public static ResultVo<Object> error(String msg, int code) {
        return new ResultVo<>(msg, code, null);
    }

    /**
     * 失败 - 消息 + 数据
     */
    public static ResultVo<Object> error(String msg, Object data) {
        return new ResultVo<>(msg, StatusCode.ERROR_CODE, data);
    }

    /**
     * 失败 - 全自定义
     */
    public static ResultVo<Object> error(String msg, int code, Object data) {
        return new ResultVo<>(msg, code, data);
    }
}