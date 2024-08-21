package com.framework.cloud.application.impl;

import com.framework.cloud.application.OcrService;
import com.framework.cloud.domain.model.CardType;
import com.framework.cloud.domain.model.CountryModel;
import com.framework.cloud.interfaces.vo.OcrVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author Lcc 2024/8/21 09:42
 */
@Slf4j
@Service
public class OcrServiceImpl implements OcrService {

    @Override
    public OcrVO ocr(MultipartFile image, CountryModel countryModel, CardType cardType, Map<String, String> headers) {
        //TODO
        return null;
    }
}
