package com.framework.cloud.interfaces.web;

import com.framework.cloud.application.FaceMaskService;
import com.framework.cloud.domain.model.basic.ComResult;
import com.framework.cloud.infrastructure.utils.MultipartFileHelper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.seeta.proxy.MaskDetectorProxy;
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
 * 口罩检测
 *
 * @author youtao531 2023/5/20 14:57
 */
@Slf4j
@RestController
@ApiSupport(order = 4)
@Tag(name = "口罩检测", description = "口罩检测")
@RequestMapping(value = "/api/faces/mask", produces = MediaType.APPLICATION_JSON_VALUE)
public class FaceMaskController {

    @Resource
    private FaceMaskService faceMaskService;

    @PostMapping
    @ApiOperationSupport(order = 1)
    @Operation(summary = "口罩检测", description = "识别人脸是否戴口罩")
    @Parameters(value = {@Parameter(name = "faceImage", description = "人脸照片", in = ParameterIn.QUERY)})
    public ComResult<MaskDetectorProxy.MaskItem> maskDetector(MultipartFile faceImage) {
        log.info("口罩检测 开始");
        Assert.notNull(faceImage, "上传人脸照片不能为空");
        MaskDetectorProxy.MaskItem result = faceMaskService.maskDetector(faceImage);
        Map<String, Object> imageInfo = MultipartFileHelper.imageInfo(faceImage);
        log.info("口罩检测结果 {}={}", imageInfo, result);
        return ComResult.ok(result);
    }
}
