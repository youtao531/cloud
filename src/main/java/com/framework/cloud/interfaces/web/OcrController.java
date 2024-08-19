package com.framework.cloud.interfaces.web;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author youtao531 2024/8/19 10:13
 */
@Slf4j
@RestController
@ApiSupport(order = 1)
@Tag(name = "文本提取", description = "文本提取")
@RequestMapping(value = "/api/ocr", produces = MediaType.APPLICATION_JSON_VALUE)
public class OcrController {
}