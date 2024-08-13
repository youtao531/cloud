package com.framework.cloud.infrastructure.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author youtao531 2023/5/20 14:57
 */
@Slf4j
@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "ok";
    }
}
