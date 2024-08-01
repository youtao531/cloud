package com.makto.seetaface.application;

import com.makto.seetaface.domain.model.CardType;
import com.makto.seetaface.domain.model.CountryModel;
import com.makto.seetaface.interfaces.vo.OcrVO;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author Lcc 2023/10/12 16:59
 */
public interface OcrService {

    /**
     * 百度飞桨OCR文字提取
     *
     * @param image        照片信息
     * @param countryModel 国家信息
     * @param cardType     卡片类型
     * @param headers      请求标头
     */
    OcrVO ocr(MultipartFile image, CountryModel countryModel, @Nullable CardType cardType, Map<String, String> headers);
}
