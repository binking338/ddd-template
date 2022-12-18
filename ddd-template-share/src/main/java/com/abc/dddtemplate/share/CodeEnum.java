package com.abc.dddtemplate.share;

/**
 * Api 状态码
 * @author <template/>
 * @date
 */
public enum CodeEnum {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 失败
     */
    FAIL(1001, "失败"),
    /**
     * 不合法参数
     */
    PARAM_INVALIDATE(2, "不合法参数"),
    /**
     * 运营后台
     */
    MANAGER_TOKEN_EXPIRE(1, "登录过期，请重新登录"),
    /**
     * 用户端
     */
    USER_TOKEN_EXPIRE(291, "登录过期，请重新登录"),
    /**
     * 未登录，请先登录
     */
    UN_LOGIN(1002, "未登录，请先登录"),
    /**
     * 消息不能读取
     */
    MESSAGE_NOT_READ(407, "消息不能读取"),
    /**
     * 不支持当前请求方法
     */
    METHOD_NOT_SUPPORTED(405, "不支持当前请求方法"),
    /**
     * 404
     */
    NOT_FOUND(404, "没找到请求"),
    /**
     * 约束未通过
     */
    SPECIFICATION_UNSATISFIED(9000, "约束不满足"),
    /**
     * 系统异常
     */
    ERROR(9999, "系统异常");

    private final Integer code;
    private final String value;

    CodeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
