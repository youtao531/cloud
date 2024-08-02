package com.makto.seetaface.interfaces.web;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.makto.seetaface.application.FaceSimilarityService;
import com.makto.seetaface.domain.model.basic.ComResult;
import com.makto.seetaface.infrastructure.utils.MultipartFileHelper;
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
@ApiSupport(order = 8)
@Tag(name = "人脸比对", description = "人脸比对")
@RequestMapping(value = "/api/faces/similarity", produces = MediaType.APPLICATION_JSON_VALUE)
public class FaceSimilarityController {

    @Resource
    private FaceSimilarityService faceSimilarityService;

    @PostMapping
    @ApiOperationSupport(order = 1)
    @Operation(summary = "相似检测", description = "相似检测")
    @Parameters(value = {
            @Parameter(name = "face1", description = "证件人脸照片", in = ParameterIn.QUERY),
            @Parameter(name = "face2", description = "活体人脸照片", in = ParameterIn.QUERY)})
    public ComResult<Float> faceRecognizer(MultipartFile face1, MultipartFile face2) {
        log.info("人脸相似检测 开始");
        Assert.notNull(face1, "上传人脸照片1不能为空");
        Assert.notNull(face2, "上传人脸照片2不能为空");
        Float result = faceSimilarityService.faceRecognizer(face1, face2);
        Map<String, Object> imageInfo1 = MultipartFileHelper.imageInfo(face1);
        Map<String, Object> imageInfo2 = MultipartFileHelper.imageInfo(face2);
        log.info("人脸相似检测 结果 {},{}={}", imageInfo1, imageInfo2, result);
        return ComResult.ok(result);
    }
}