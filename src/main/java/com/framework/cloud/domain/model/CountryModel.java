package com.framework.cloud.domain.model;

import com.framework.cloud.infrastructure.exception.constant.ErrorException;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.LocaleUtils;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Country Model
 *
 * @author youtao531 on 2023/6/30 15:22
 */
@Data
@Builder
@Accessors(chain = true)
public class CountryModel implements Serializable {

    public static final List<String> excludeCountries = List.of("AQ", "BV", "GS", "HM", "MV", "TF");

    public static final List<String> allowedCountries = List.of("GH", "TZ");

    public static final List<String> allCountryCodes = Arrays.stream(Locale.getISOCountries())
            .filter(country -> !excludeCountries.contains(country))
            .sorted()
            .toList();

    private String country;         //国家代码(2位)
    private String iso3Country;     //国家代码(3位)
    private String displayCountry;  //国家简称

    /**
     * @param countryIso 两个字母的国家代码
     */
    @NonNull
    public static CountryModel ofCountry(String countryIso) {
        return ofCountry(countryIso, LocaleUtils.toLocale("zh"));
    }

    /**
     * @param countryIso    两个字母的国家代码
     * @param displayLocale 显示的语言(仅对国家全称生效)
     */
    @NonNull
    public static CountryModel ofCountry(String countryIso, Locale displayLocale) {
        if (StrUtil.isBlank(countryIso)) {
            throw new ErrorException(ComCodes.COMMON_FAIL.getCode(), "NOT_SUPPORT_COUNTRY_CODE");
        }
        countryIso = countryIso.toUpperCase();
        if (excludeCountries.contains(countryIso) || !allowedCountries.contains(countryIso)) {
            throw new ErrorException(ComCodes.COMMON_FAIL.getCode(), "NOT_SUPPORT_COUNTRY_CODE");
        }
        List<Locale> locales = LocaleUtils.languagesByCountry(countryIso);
        if (CollUtil.isEmpty(locales)) {
            throw new ErrorException(ComCodes.COMMON_FAIL.getCode(), "NOT_SUPPORT_COUNTRY_CODE");
        }

        Locale locale = locales.getFirst();
        String displayCountry = null == displayLocale ? locale.getDisplayCountry() : locale.getDisplayCountry(displayLocale);
        return CountryModel.builder()
                .country(locale.getCountry())
                .iso3Country(locale.getISO3Country())
                .displayCountry(displayCountry)
                .build();
    }
}
