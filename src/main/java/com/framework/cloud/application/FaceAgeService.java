package com.framework.cloud.application;

import org.springframework.web.multipart.MultipartFile;

/**
 * 人脸年龄检测
 *
 * @author youtao531 on 2023/9/25 11:16
 */
public interface FaceAgeService {

    /**
     * 人脸年龄判断
     *
     * @param faceImage 人脸图片文件
     * @return List<Integer> 多个人脸的年龄
     */
    Integer agePredictor(MultipartFile faceImage);
}
