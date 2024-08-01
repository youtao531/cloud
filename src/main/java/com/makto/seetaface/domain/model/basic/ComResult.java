package com.makto.seetaface.domain.model.basic;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 结果封装
 *
 * @author Yt on 2022/9/20 16:49
 */
@Data
public class ComResult<T> implements Serializable {

    @Schema(title = "请求响应代码", description = "请求响应代码，0-请求成功，非0请求失败", requiredMode = Schema.RequiredMode.REQUIRED)
    private int code = ComCodes.OK.getCode();
    @Schema(title = "请求响应状态", description = "请求响应状态，OK-请求成功，非OK请求失败", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status = ComCodes.OK.getStatus();
    @Schema(title = "请求响应消息", description = "请求响应消息", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
    @Schema(title = "请求响应数据", description = "请求响应数据")
    private T result;

    /**
     * 响应成功
     * <pre>
     * {
     *   "code": 0,
     *   "status": "OK",
     *   "message": "ok",
     *   "result": null
     * }
     * </pre>
     *
     * @param <T> 范型
     * @return 响应成功
     */
    public static <T> ComResult<T> ok() {
        return ok(null);
    }

    /**
     * 响应成功
     * <pre>
     * {
     *   "code": 0,
     *   "status": "OK",
     *   "message": "ok",
     *   "result": ${result}
     * }
     * </pre>
     *
     * @param result 业务数据
     * @param <T>    范型
     * @return 响应成功
     */
    public static <T> ComResult<T> ok(T result) {
        return build(ComCodes.OK, result);
    }

    /**
     * 响应失败
     * <pre>
     * {
     *   "code": 1,
     *   "status": "COMMON_FAIL",
     *   "message": "fail",
     *   "result": null
     * }
     * </pre>
     *
     * @param <T> 范型
     * @return 响应失败
     */
    public static <T> ComResult<T> fail() {
        return fail(ComCodes.COMMON_FAIL);
    }

    /**
     * 响应失败
     * <pre>
     * {
     *   "code": ${code},
     *   "status": "${status}",
     *   "message": "${此消息字段根据code查询对应语言的资源文件}",
     *   "result": null
     * }
     * </pre>
     *
     * @param codes 失败编码（非0的数字）
     * @param args  消息内的动态参数
     * @param <T>   范型
     * @return 响应失败
     */
    public static <T> ComResult<T> fail(ComCodes codes, String... args) {
        return build(codes, null, args);
    }

    /**
     * 构造 国际化消息
     *
     * @param codes  错误编码
     * @param result 响应数据
     * @param args   动态参数
     * @param <T>    响应数据范型
     * @return 国际化消息
     */
    private static <T> ComResult<T> build(ComCodes codes, T result, String... args) {
        String i18nMsg = MessageSourceHelper.getMessage(codes.getStatus(), args);
        if (StrUtil.isBlank(i18nMsg) && ComCodes.OK != codes) {
            i18nMsg = MessageSourceHelper.getMessage(ComCodes.COMMON_FAIL.getStatus(), args);
        }
        ComResult<T> comResult = new ComResult<>();
        comResult.setCode(codes.getCode());
        comResult.setStatus(codes.getStatus());
        comResult.setMessage(i18nMsg);
        comResult.setResult(result);
        return comResult;
    }
}
