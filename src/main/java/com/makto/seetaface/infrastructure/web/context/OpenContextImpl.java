package com.makto.seetaface.infrastructure.web.context;

import com.makto.seetaface.domain.model.basic.ComHeaders;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lcc 2023/4/26 14:46
 */
public class OpenContextImpl implements OpenContext {

    private final Map<String, Object> parameterMap = new HashMap<>();
    private final Map<String, String> headerMap = new HashMap<>();
    private final String remoteIp;

    public OpenContextImpl(Map<String, Object> paramMap, Map<String, String> headerMap, String remoteIp) {
        this.parameterMap.putAll(paramMap);
        headerMap.forEach((k, v) -> {
            if (ComHeaders.allHeaders.contains(k.toUpperCase())) {
                this.headerMap.put(k.toUpperCase(), v);
                this.headerMap.remove(k);
            } else {
                this.headerMap.put(k, v);
            }
        });
        this.remoteIp = remoteIp;
        String nonce = this.headerMap.get(ComHeaders.NONCE_KEY);
        MDC.put(ComHeaders.NONCE_KEY, nonce);
    }

    @Override
    public Map<String, Object> getParameterMap() {
        return this.parameterMap;
    }

    @Override
    public Map<String, String> getHeaderMap() {
        return this.headerMap;
    }

    @Override
    public Map<String, String> getCustomHeaderMap() {
        Map<String, String> custom = new HashMap<>();
        this.headerMap.forEach((k, v) -> {
            if (ComHeaders.allHeaders.contains(k.toUpperCase())) {
                custom.put(k.toUpperCase(), v);
            }
        });
        return custom;
    }

    @Override
    public String getRemoteIp() {
        return this.remoteIp;
    }
}
