package com.makto.seetaface.infrastructure.web.context;

import cn.hutool.core.util.StrUtil;
import com.makto.seetaface.domain.model.basic.ComHeaders;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Locale;
import java.util.Map;

/**
 * 获取开放平台请求参数。
 *
 * @author tanghc
 */
public interface OpenContext {

    /**
     * 返回 所有的请求参数
     * <p>
     * 包括 各种媒体类型请求的参数（application/json，text/plain，）
     * </p>
     *
     * @return 所有的请求参数
     */
    Map<String, Object> getParameterMap();

    /**
     * 返回 所有请求标头
     *
     * @return 所有请求标头
     */
    Map<String, String> getHeaderMap();

    /**
     * 返回 自定义请求标头
     *
     * @return 自定义请求标头
     */
    Map<String, String> getCustomHeaderMap();

    /**
     * 获取 某个参数值
     *
     * @param name 参数名称
     * @return 某个参数值（若没有则返回null）
     */
    default String getParameter(String name) {
        Object value = getParameterMap().get(name);
        return value == null ? null : value.toString();
    }

    /**
     * 获取 某个请求标头值
     *
     * @param name 请求标头名称
     * @return 某个请求标头值（若没有则返回null）
     */
    default String getHeader(String name) {
        return getHeaderMap().get(name);
    }

    /**
     * 返回 时间戳(UTC时区)
     *
     * @return 时间戳(UTC时区)
     */
    default Long getTimestamp() {
        String timestampStr = getHeader(ComHeaders.TIME_KEY);
        if (StrUtil.isBlank(timestampStr)) {
            return null;
        }
        return Long.parseLong(timestampStr);
    }

    /**
     * 返回 随机串
     *
     * @return 随机串(每次请求唯一)
     */
    default String getNonce() {
        return getHeader(ComHeaders.NONCE_KEY);
    }

    /**
     * 返回 国家代码
     *
     * @return 国家代码
     */
    default String getCountry() {
        return getHeader(ComHeaders.COUNTRY_KEY);
    }

    /**
     * 返回 用户ID
     *
     * @return 用户ID
     */
    default String getUserId() {
        return getHeader(ComHeaders.UID_KEY);
    }

    /**
     * 返回 设备语言
     * <p>
     * 首先从请求头中解析，不存在则从请求上下文中解析获取
     * </p>
     *
     * @return 设备语言
     */
    default Locale getLocale(HttpServletRequest request) {
        String locale = null;
        Locale localeObj = RequestContextUtils.getLocale(request);
        if (StrUtil.isNotBlank(localeObj.getLanguage())) {
            locale = localeObj.getLanguage();
        }
        if (StrUtil.isNotBlank(locale)) {
            String[] split = locale.split("#");
            String str = split[0];
            if (str.endsWith("_")) {
                locale = str.substring(0, str.length() - 1);
            }
        }
        return LocaleUtils.toLocale(locale);
    }

    /**
     * 返回 真实IP
     *
     * @return 真实IP
     */
    String getRemoteIp();
}
