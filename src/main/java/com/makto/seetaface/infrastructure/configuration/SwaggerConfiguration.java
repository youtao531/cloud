package com.makto.seetaface.infrastructure.configuration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.makto.seetaface.domain.model.basic.ComHeaders;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
 * @author Yt on 2022/12/7 14:21
 */
@Configuration
public class SwaggerConfiguration {

    public static final String X_ORDER_KEY = "x-order";
    private static final Map<String, String> tagOrderMap = MapUtil.newConcurrentHashMap();

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

    /**
     * 设置方法公共请求头
     * <p>
     * 为了接口文档的简便和可读性，隐藏这些请求头参数。对接的时候告知对接方规则。
     * </p>
     */
    private static void setComHeaders(Operation operation) {
        //公共请求头参数
        HeaderParameter country = buildHeaderParameter(ComHeaders.COUNTRY_KEY, "国家代码，例如：TZ、GH、CI等", 0, 2, 2);
        HeaderParameter timestamp = buildHeaderParameter(ComHeaders.TIME_KEY, "时间戳，13位毫秒值。例如：1653498543312", 2, 13, 13);
        HeaderParameter nonce = buildHeaderParameter(ComHeaders.NONCE_KEY, "随机串，一次性的唯一字符串。例如：c83dd35612927090a14c754caae6af12", 3, 8, 64);
        List<HeaderParameter> parameters = ListUtil.toList(country, timestamp, nonce);
        parameters.forEach(operation::addParametersItem);
    }

    /**
     * 构建 公共请求头
     *
     * @param name        请求头名称
     * @param description 描述
     * @param xOrder      排序
     * @param min         最小长度（小于0则不限制）
     * @param max         最大长度（小于0则不限制）
     * @return 公共请求头
     */
    private static HeaderParameter buildHeaderParameter(String name, String description, int xOrder, int min, int max) {
        StringSchema schema = new StringSchema();
        if (min >= 0) {
            schema.minLength(min);
        }
        if (max >= 0) {
            schema.maxLength(max);
        }

        HeaderParameter parameter = new HeaderParameter();
        parameter.name(name);
        parameter.description(description);
        parameter.required(true);
        parameter.schema(schema);
        parameter.addExtension(X_ORDER_KEY, String.valueOf(xOrder));
        return parameter;
    }

    @Bean
    public GlobalOperationCustomizer globalOperationCustomizer() {
        return (operation, handlerMethod) -> {
            //设置方法公共请求头
            setComHeaders(operation);

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

            //            //方法排序（无效）
            //            Paths apiPaths = openApi.getPaths();
            //            apiPaths.forEach((name, item) -> {
            //                List<Operation> operations = item.readOperations();
            //                Map<PathItem.HttpMethod, Operation> httpMethodOperationMap = item.readOperationsMap();
            //            });
            //            TreeMap<String, PathItem> sort = MapUtil.sort(apiPaths);
            //            Paths newPaths = new Paths();
            //            newPaths.putAll(sort);
            //            openApi.setPaths(newPaths);

            //排序控制器Tag
            sortTags(openApi);
        };
    }
}
