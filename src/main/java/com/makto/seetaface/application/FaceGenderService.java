package com.makto.seetaface.application;

import com.seeta.proxy.GenderPredictorProxy;
import org.springframework.web.multipart.MultipartFile;

/**
 * 人脸性别检测
 *
 * @author Lcc 2023/9/25 11:16
 */
public interface FaceGenderService {

    /**
     * 人脸性别判断
     *
     * @param faceImage 人脸图片文件
     * @return GenderPredictorProxy.GenderItem 人脸的性别
     */
    GenderPredictorProxy.GenderItem genderPredictor(MultipartFile faceImage);
}
