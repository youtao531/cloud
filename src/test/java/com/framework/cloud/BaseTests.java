package com.framework.cloud;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * @author Lcc 2024/8/22 10:17
 */
@Slf4j
public class BaseTests {


    @BeforeAll
    protected static void setUp() {
        log.info("----------------------->>> setUp");
    }

    @AfterAll
    protected static void setAfter() {
        log.info("----------------------->>> setAfter");
    }
}
