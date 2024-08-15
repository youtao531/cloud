package com.framework.cloud.application;

import com.seeta.proxy.QualityOfLBNProxy;
import org.springframework.web.multipart.MultipartFile;

/**
 * 人脸质量检测
 *
 * @author youtao531 on 2024/7/25 11:16
 */
public interface FaceQualityService {

    /**
     * 人脸质量检测
     *
     * @param faceImage 人脸图片文件
     * @return QualityOfLBNProxy.LBNClass 人脸的质量
     */
    QualityOfLBNProxy.LBNClass faceQuality(MultipartFile faceImage);
}
