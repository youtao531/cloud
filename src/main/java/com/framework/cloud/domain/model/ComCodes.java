package com.framework.cloud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 请求响应码定义
 * <p>
 *      HTTP状态码统一响应为200，其他情况通过以下code返回具体的业务错误码
 * </p>
 * @author youtao531 on 2023/1/5 17:13
 * @formatter:off
 */
@Getter
@AllArgsConstructor
public enum ComCodes {

    OK(                     0, "请求成功"),
    COMMON_FAIL(            1, "通用失败"),
    BAD_REQUEST(          400, "一般性客户端错误"),
    UNAUTHORIZED(         401, "未认证"),
    FORBIDDEN(            403, "禁止操作"),
    NOT_FOUND(            404, "资源未找到"),
    METHOD_NOT_ALLOWED(   405, "请求方式不支持"),
    TOO_MANY_REQUESTS(    429, "请求太频繁"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    BAD_GATEWAY(          502, "网关请求上游服务时出错"),
    SERVICE_UNAVAILABLE(  503, "服务不可用重启或维护中"),
    GATEWAY_TIMEOUT(      503, "网关请求上游服务超时"),
    ;

    private final int code;
    private final String desc;

    /**
     * 根据标识获取枚举
     *
     * @param code 响应码
     * @return 枚举
     */
    public static ComCodes getEnum(Integer code) {
        return getEnum(code, ComCodes.COMMON_FAIL);
    }

    /**
     * 根据标识获取枚举
     *
     * @param code         响应码
     * @param defaultValue 默认响应码
     * @return 枚举
     */
    public static ComCodes getEnum(Integer code, ComCodes defaultValue) {
        return Arrays.stream(values())
                .filter(x -> Objects.equals(x.getCode(), code))
                .findFirst()
                .orElse(defaultValue);
    }

    public String getStatus() {
        return this.name();
    }
}
