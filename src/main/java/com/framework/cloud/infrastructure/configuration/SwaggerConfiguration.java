package com.framework.cloud.infrastructure.configuration;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 接口文档配置
 *
 * @author youtao531 on 2022/12/7 14:21
 */
@Configuration
public class SwaggerConfiguration {

    public static final String X_ORDER_KEY = "x-order";
    private static final Map<String, String> tagOrderMap = MapUtil.newSafeConcurrentHashMap();

    /**
     * 排序控制器Tag
     */
    private static void sortTags(OpenAPI openApi) {
        List<Tag> apiTags = openApi.getTags();
        if (CollUtil.isNotEmpty(apiTags)) {
            apiTags.forEach(tag -> {
                int xOrder = MapUtil.getInt(tagOrderMap, tag.getName(), 0);
                tag.addExtension(X_ORDER_KEY, String.valueOf(xOrder));
            });
            apiTags.sort((o1, o2) -> {
                Integer a1 = MapUtil.getInt(o1.getExtensions(), X_ORDER_KEY, 0);
                Integer a2 = MapUtil.getInt(o2.getExtensions(), X_ORDER_KEY, 0);
                return a1.compareTo(a2);
            });
        }
    }

    /**
     * 重命名分组名称
     */
    private static void renameGroup(OpenAPI openApi) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            HttpServletRequest request = requestAttributes.getRequest();
            String requestURI = request.getRequestURI();// /v3/api-docs/k

            SpringDocConfigProperties springDocConfigProperties = SpringUtil.getBean(SpringDocConfigProperties.class);
            String path = springDocConfigProperties.getApiDocs().getPath();
            String group = requestURI.replaceAll(path, "").replaceAll("/", "");

            // 去重
            List<SpringDocConfigProperties.GroupConfig> groupConfigs = springDocConfigProperties.getGroupConfigs().stream()
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                            new TreeSet<>(Comparator.comparing(SpringDocConfigProperties.GroupConfig::getGroup))), ArrayList::new));
            Map<String, SpringDocConfigProperties.GroupConfig> groupConfigMap = groupConfigs.stream()
                    .collect(Collectors.toMap(SpringDocConfigProperties.GroupConfig::getGroup, Function.identity()));
            SpringDocConfigProperties.GroupConfig groupConfig = groupConfigMap.get(group);

            Info info = new Info()
                    .title(groupConfig.getDisplayName())
                    .description(groupConfig.getDisplayName())
                    .version("v1");
            openApi.info(info);
        }
    }

    /**
     * 设置控制器Tag排序字段
     */
    private static void setTagsOrder(Operation operation, HandlerMethod handlerMethod) {
        //控制器上的注解
        Class<?> beanType = handlerMethod.getBeanType();
        ApiSupport apiSupport = beanType.getAnnotation(ApiSupport.class);
        ApiSort apiSort = beanType.getAnnotation(ApiSort.class);
        int apiOrder = 0;
        if (null != apiSort) {
            apiOrder = apiSort.value();
        }
        if (null != apiSupport) {
            apiOrder = apiSupport.order();
        }
        for (String tag : operation.getTags()) {
            tagOrderMap.put(tag, String.valueOf(apiOrder));
        }
    }

    @Bean
    public GlobalOperationCustomizer globalOperationCustomizer() {
        return (operation, handlerMethod) -> {
            //方法上面的注解
            ApiOperationSupport apiOperationSupport = handlerMethod.getMethodAnnotation(ApiOperationSupport.class);
            int apiOperationOrder = null == apiOperationSupport ? 0 : apiOperationSupport.order();
            operation.addExtension(X_ORDER_KEY, String.valueOf(apiOperationOrder));

            //设置控制器Tag排序字段
            setTagsOrder(operation, handlerMethod);

            return operation;
        };
    }

    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            //重命名分组名称
            renameGroup(openApi);

            //排序控制器Tag
            sortTags(openApi);
        };
    }
}
