package com.framework.cloud.application;

import com.framework.cloud.BaseTests;
import com.framework.cloud.domain.model.CardType;
import com.framework.cloud.domain.model.CountryModel;
import com.framework.cloud.interfaces.vo.OcrVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

@Slf4j
@SpringBootTest
class OcrServiceTests extends BaseTests {

    @Resource
    private OcrService ocrService;

    @Test
    void test() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());
        OcrVO ocrVO = ocrService.ocr(mockMultipartFile, CountryModel.ofCountry("GH"), CardType.ID_FRONT, null);
        log.info(JSONUtil.toJsonStr(ocrVO));
    }
}
