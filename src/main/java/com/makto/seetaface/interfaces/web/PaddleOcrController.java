package com.makto.seetaface.interfaces.web;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.makto.seetaface.application.OcrService;
import com.makto.seetaface.domain.model.CountryModel;
import com.makto.seetaface.domain.model.basic.ComResult;
import com.makto.seetaface.infrastructure.utils.MultipartFileHelper;
import com.makto.seetaface.infrastructure.web.context.OpenContext;
import com.makto.seetaface.infrastructure.web.context.ServiceContext;
import com.makto.seetaface.interfaces.vo.OcrVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author Lcc 2023/10/12 15:43
 */
@Slf4j
@RestController
@ApiSupport(order = 9)
@Tag(name = "飞桨OCR", description = "飞桨OCR")
@RequestMapping(value = "/api/ocr", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaddleOcrController {

    @Resource
    private OcrService ocrService;

    @PostMapping
    @ApiOperationSupport(order = 1)
    @Operation(summary = "照片文字提取", description = "飞桨OCR - 照片文字提取")
    @Parameters(value = {
            @Parameter(name = "image", description = "照片", in = ParameterIn.DEFAULT)})
    public ComResult<OcrVO> ocr(MultipartFile image) {
        log.info("照片文字提取 开始");
        Assert.notNull(image, "上传照片 不能为空");

        OpenContext openContext = ServiceContext.getCurrentContext().getOpenContext();
        Map<String, String> headerMap = openContext.getHeaderMap();
        CountryModel countryModel = CountryModel.ofCountry(openContext.getCountry());
        OcrVO result = ocrService.ocr(image, countryModel, null, headerMap);
        Map<String, Object> imageInfo = MultipartFileHelper.imageInfo(image);
        log.info("照片文字提取 结果 {}={}", imageInfo, result);
        return ComResult.ok(result);
    }
}