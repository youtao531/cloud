package com.framework.cloud.domain.model;

import com.framework.cloud.domain.model.basic.ComCodes;
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
 * @author youtao531 2023/6/30 15:22
 */
@Data
@Builder
@Accessors(chain = true)
public class CountryModel implements Serializable {

    //"AQ" + "ATA"  //南极洲（南纬60度以南的领土）
    //"BV" + "BVT"  //布韦岛（布韦托亚）
    //"GS" + "SGS"  //南乔治亚岛和南桑威奇群岛
    //"HM" + "HMD"  //赫德岛和麦克唐纳群岛
    //"MV" + "MDV"  //马尔代夫共和国
    //"TF" + "ATF"  //法属南部领地
    public static final List<String> excludeCountries = List.of("AQ", "BV", "GS", "HM", "MV", "TF");
    /**
     * 受支持的国家
     */
    public static final List<String> allowedCountries = List.of("GH", "TZ");
    /**
     * 所有受支持的国家代码
     */
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
