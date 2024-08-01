package com.makto.seetaface.application.impl;

import com.makto.seetaface.application.OcrService;
import com.makto.seetaface.domain.model.CardType;
import com.makto.seetaface.domain.model.CountryModel;
import com.makto.seetaface.domain.model.basic.ComCodes;
import com.makto.seetaface.infrastructure.exception.constant.ErrorException;
import com.makto.seetaface.infrastructure.properties.BizMaktoProperties;
import com.makto.seetaface.infrastructure.sdk.paddle.PaddleCardHelper;
import com.makto.seetaface.infrastructure.sdk.paddle.PaddleOCRHelper;
import com.makto.seetaface.infrastructure.sdk.paddle.model.BlockMap;
import com.makto.seetaface.infrastructure.sdk.paddle.model.OcrBlock;
import com.makto.seetaface.infrastructure.sdk.paddle.model.PaddleOCRResult;
import com.makto.seetaface.infrastructure.utils.MultipartFileHelper;
import com.makto.seetaface.interfaces.vo.OcrVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * OCR服务
 *
 * @author Lcc 2023/10/12 16:59
 */
@Slf4j
@Service
public class OcrServiceImpl implements OcrService {

    @Resource
    private BizMaktoProperties bizMaktoProperties;

    @Override
    public OcrVO ocr(MultipartFile image, CountryModel countryModel, @Nullable CardType cardType, Map<String, String> headers) {
        Instant begin = Instant.now();

        String apiUrl = String.format("%s/ocr/prediction", bizMaktoProperties.getPaddleUrl());

        String base64 = MultipartFileHelper.toBytesBase64(image);
        PaddleOCRResult ocrResult = PaddleOCRHelper.paddleOcr(apiUrl, base64, headers);
        if (ocrResult.fail()) {
            throw new ErrorException(ComCodes.INTERNAL_SERVER_ERROR.getCode(), ocrResult.getErr_msg());
        }

        List<OcrBlock> blocks = PaddleOCRHelper.parseOcrResult(ocrResult);
        int blockSize = blocks.size();
        long execMillis = ChronoUnit.MILLIS.between(begin, Instant.now());
        CardType type = null != cardType ? cardType : PaddleCardHelper.of(countryModel.getCountry()).fetchCardType(blocks);
        BlockMap blockMap = PaddleCardHelper.of(countryModel.getCountry()).fetchBlockInfo(blocks, type);

        return new OcrVO()
                .setExecMillis(execMillis)
                .setCountryCode(countryModel.getCountry())
                .setCardType(type)
                .setBlockSize(blockSize)
                .setBlockMap(blockMap)
                .setBlocks(blocks)
                ;
    }
}