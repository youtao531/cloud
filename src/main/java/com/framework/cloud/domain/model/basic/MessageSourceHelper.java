package com.framework.cloud.domain.model.basic;

import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.Locale;

/**
 * 国际化消息
 *
 * @author youtao531 on 2022/12/10 17:40
 */
public class MessageSourceHelper {

    /**
     * 获取 国际化消息
     *
     * @param key  资源文件的属性Key
     * @param args 参数，例如：#{key}=hello {0}, {1}
     * @return 国际化消息
     */
    public static String getMessage(String key, String... args) {
        MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
        Locale locale = LocaleContextHolder.getLocale();
        Object[] array = null;
        if (ArrayUtil.isNotEmpty(args)) {
            array = Arrays.stream(args).map(StrUtil::toStringOrEmpty).toArray();
        }
        return messageSource.getMessage(key, array, locale);
    }
}
