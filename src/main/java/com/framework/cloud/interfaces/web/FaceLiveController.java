package com.framework.cloud.interfaces.web;

import com.framework.cloud.application.FaceLiveService;
import com.framework.cloud.domain.model.basic.ComResult;
import com.framework.cloud.infrastructure.utils.MultipartFileHelper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.seeta.sdk.FaceAntiSpoofing;
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

@Slf4j
@RestController
@ApiSupport(order = 3)
@Tag(name = "静默活体", description = "静默活体")
@RequestMapping(value = "/api/faces/live", produces = MediaType.APPLICATION_JSON_VALUE)
public class FaceLiveController {

    @Resource
    private FaceLiveService faceLiveService;

    @PostMapping
    @ApiOperationSupport(order = 1)
    @Operation(summary = "静默活体检测", description = "静默活体检测")
    @Parameters(value = {@Parameter(name = "faceImage", description = "人脸照片", in = ParameterIn.QUERY)})
    public ComResult<FaceAntiSpoofing.Status> faceAntiSpoofing(MultipartFile faceImage) {
        log.info("静默活体检测 开始");
        Assert.notNull(faceImage, "上传人脸照片不能为空");
        FaceAntiSpoofing.Status result = faceLiveService.faceAntiSpoofing(faceImage);
        Map<String, Object> imageInfo = MultipartFileHelper.imageInfo(faceImage);
        log.info("静默活体检测 结果 {}={}", imageInfo, result);
        return ComResult.ok(result);
    }
}
