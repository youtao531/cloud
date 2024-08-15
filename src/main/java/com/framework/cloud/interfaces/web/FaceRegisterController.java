package com.framework.cloud.interfaces.web;

import com.framework.cloud.application.FaceRegisterService;
import com.framework.cloud.domain.model.basic.ComResult;
import com.framework.cloud.infrastructure.utils.MultipartFileHelper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
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

import java.io.IOException;
import java.util.Map;

/**
 * 人脸搜索 1：N
 * 将底库人脸头像向量特征注册到内存中，项目重启后注册的人脸将失效
 * 可以将人脸向量特征保存到数据库，项目启动时加载到内存
 *
 * @author youtao531 on 2024/7/25 14:27
 */
@Slf4j
@RestController
@ApiSupport(order = 6)
@Tag(name = "人脸注册", description = "人脸注册")
@RequestMapping(value = "/api/faces/register", produces = MediaType.APPLICATION_JSON_VALUE)
public class FaceRegisterController {

    @Resource
    private FaceRegisterService faceRegisterService;

    @PostMapping
    @ApiOperationSupport(order = 1)
    @Operation(summary = "人脸搜索底库注册", description = "人脸搜索底库注册")
    @Parameters(value = {@Parameter(name = "faceImage", description = "人脸照片", in = ParameterIn.QUERY)})
    public ComResult<String> faceRegister(MultipartFile faceImage) throws IOException {
        log.info("人脸搜索底库注册 开始");
        Assert.notNull(faceImage, "上传人脸照片不能为空");
        String result = faceRegisterService.registerFace(faceImage);
        Map<String, Object> imageInfo = MultipartFileHelper.imageInfo(faceImage);
        log.info("人脸搜索底库注册 结果 {}={}", imageInfo, result);
        return ComResult.ok(result);
    }
}