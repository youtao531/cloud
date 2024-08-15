package com.framework.cloud.application;

import org.springframework.web.multipart.MultipartFile;

/**
 * 人脸相似度检测
 *
 * @author youtao531 on 2023/9/25 11:16
 */
public interface FaceSimilarityService {

    /**
     * 人脸对比，1：1
     *
     * @param face1 人脸图片文件
     * @param face2 人脸图片文件
     * @return Float 分数 0~1
     */
    Float faceRecognizer(MultipartFile face1, MultipartFile face2);
}
