package com.framework.cloud.domain.core;

import java.util.List;

/**
 * 接口 通用请求标头定义
 *
 * @author youtao531 on 2023/1/7 10:36
 */
public final class ComHeaders {

    public static final String TIME_KEY = "X-TIMESTAMP";    //时间戳 13位毫秒值
    public static final String NONCE_KEY = "X-NONCE";        //随机串 一次性的唯一字符串

    public static final List<String> allHeaders = List.of(TIME_KEY, NONCE_KEY);
}