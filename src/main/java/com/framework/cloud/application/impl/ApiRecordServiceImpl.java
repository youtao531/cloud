package com.framework.cloud.application.impl;

import com.framework.cloud.application.ApiRecordService;
import com.framework.cloud.domain.model.ApiRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Lcc 2024/9/25 16:58
 */
@Slf4j
@Service
public class ApiRecordServiceImpl implements ApiRecordService {

    @Async
    @Override
    public void record(ApiRecord record) {
        // TODO save to db.
        log.info("record: {}", record);
    }
}
