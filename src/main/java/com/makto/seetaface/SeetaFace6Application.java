package com.makto.seetaface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

import java.util.TimeZone;

@EnableCaching          //启用注解式缓存
@ServletComponentScan   //启用自定义拦截器
@SpringBootApplication(scanBasePackages = "com.makto.seetaface")
public class SeetaFace6Application {

    static {
        if (0 != TimeZone.getDefault().getRawOffset()) {
            TimeZone utc = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(utc);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SeetaFace6Application.class, args);
    }
}