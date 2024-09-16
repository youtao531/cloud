package com.framework.cloud.application.impl;

import com.framework.cloud.application.OcrService;
import com.framework.cloud.domain.model.ComCodes;
import com.framework.cloud.domain.model.CardType;
import com.framework.cloud.domain.model.CountryModel;
import com.framework.cloud.infrastructure.exception.constant.ErrorException;
import com.framework.cloud.infrastructure.sdk.paddle.PaddleCardHelper;
import com.framework.cloud.infrastructure.sdk.paddle.PaddleOCRHelper;
import com.framework.cloud.infrastructure.sdk.paddle.model.BlockMap;
import com.framework.cloud.infrastructure.sdk.paddle.model.OcrBlock;
import com.framework.cloud.infrastructure.sdk.paddle.model.PaddleOCRResult;
import com.framework.cloud.infrastructure.utils.MultipartFileHelper;
import com.framework.cloud.interfaces.vo.OcrVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * @author Lcc 2024/8/21 09:42
 */
@Slf4j
@Service
public class OcrServiceImpl implements OcrService {

    @Override
    public OcrVO ocr(MultipartFile image, CountryModel countryModel, CardType cardType, Map<String, String> headers) {
        Instant begin = Instant.now();

        String apiUrl = "";

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
