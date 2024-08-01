package com.makto.seetaface.infrastructure.web;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.ServletRequest;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Lcc 2023/4/26 11:45
 */
public class WebParamUtil {

    public static Map<String, Object> getRequestParams(ServletRequest request) {
        Map<String, Object> paramMap = MapUtil.newHashMap();
        //如果是自定义wrapper， 从自定义wrapper中获取请求参数， 必须在interceptor拦截器中处理， 否则restful风格的请求参数获取不到。
        if (request instanceof MyHttpServletRequestWrapper) {
            paramMap = getRequestWrapperParam((MyHttpServletRequestWrapper) request);
        } else {
            // 原始请求方式获取参数
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (CollUtil.isNotEmpty(parameterMap)) {
                Set<Map.Entry<String, String[]>> entrySet = parameterMap.entrySet();
                for (Map.Entry<String, String[]> entry : entrySet) {
                    String name = entry.getKey();
                    String[] values = entry.getValue();
                    if (values.length >= 1) {
                        paramMap.put(name, values[0]);
                    }
                }
            }
        }
        return paramMap;
    }

    private static Map<String, Object> getRequestWrapperParam(MyHttpServletRequestWrapper request) {
        Map<String, Object> paramMap = MapUtil.newHashMap();

        //获取使用@RequestParam注解的参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (CollUtil.isNotEmpty(parameterMap)) {
            parameterMap.forEach((k, v) -> {
                if (Objects.nonNull(v) && v.length > 0) {
                    paramMap.put(k, v[0]);
                }
            });
        }

        //获取restful请求PATH参数，必须在interceptor拦截其中才能这样获取到restful参数
//        Object attribute = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//        if (Objects.nonNull(attribute)) {
//            Map<String, Object> attributeMap = (Map<String, Object>) attribute;
//            if (CollUtil.isNotEmpty(attributeMap)) {
//                paramMap.putAll(attributeMap);
//            }
//        }

        //从自定义wrapper中， 获取body体参数
        String bodyString = request.getBody();
        if (StrUtil.isBlank(bodyString)) {
            return paramMap;
        }

        //解析body参数
        Map<String, Object> bodyMap = parseRequestMap(bodyString);
        if (CollUtil.isEmpty(bodyMap)) {
            return paramMap;
        }
        paramMap.putAll(bodyMap);
        return paramMap;
    }

    /**
     * 解析body请求参数
     */
    private static Map<String, Object> parseRequestMap(String bodyString) {
        Map<String, Object> paramMap = MapUtil.newHashMap();

        //解析@ReqeustBody注解参数
        boolean validObject = JSONUtil.isTypeJSON(bodyString);
        if (validObject) {
            JSONObject entries = JSONUtil.parseObj(bodyString);
            paramMap.putAll(entries);
        } else {
            //解析url拼接参数 例 a=123&b=456， 没有加@RequestBoyd注解的post请求
            String[] param = bodyString.split("&");
            if (param.length == 0) {
                return paramMap;
            }
            Stream.of(param).forEach(e -> {
                String[] split = e.split("=");
                if (split.length == 0) {
                    return;
                }
                paramMap.put(split[0], split[1]);
            });
        }
        return paramMap;
    }
}
