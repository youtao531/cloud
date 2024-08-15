package com.framework.cloud.interfaces.web;

import com.framework.cloud.application.FaceGenderService;
import com.framework.cloud.domain.model.basic.ComResult;
import com.framework.cloud.infrastructure.utils.MultipartFileHelper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.seeta.proxy.GenderPredictorProxy;
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
 * 性别检测
 *
 * @author youtao531 2023/5/20 14:57
 */
@Slf4j
@RestController
@ApiSupport(order = 2)
@Tag(name = "性别检测", description = "性别检测")
@RequestMapping(value = "/api/faces/gender", produces = MediaType.APPLICATION_JSON_VALUE)
public class FaceGenderController {

    @Resource
    private FaceGenderService faceGenderService;

    @PostMapping
    @ApiOperationSupport(order = 1)
    @Operation(summary = "性别检测", description = "性别检测")
    @Parameters(value = {@Parameter(name = "faceImage", description = "人脸照片", in = ParameterIn.QUERY)})
    public ComResult<GenderPredictorProxy.GenderItem> genderPredictor(MultipartFile faceImage) {
        log.info("性别检测 开始");
        Assert.notNull(faceImage, "上传人脸照片不能为空");
        GenderPredictorProxy.GenderItem result = faceGenderService.genderPredictor(faceImage);
        Map<String, Object> imageInfo = MultipartFileHelper.imageInfo(faceImage);
        log.info("性别检测 结果 {}={}", imageInfo, result);
        return ComResult.ok(result);
    }
}
