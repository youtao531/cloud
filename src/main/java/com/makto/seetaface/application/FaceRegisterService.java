package com.makto.seetaface.application;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 人脸注册
 *
 * @author Lcc 2023/9/25 11:16
 */
public interface FaceRegisterService {

    /**
     * 注册人脸信息
     *
     * @param faceImage 人脸图片文件
     * @return 返回人脸数据的ID
     */
    String registerFace(MultipartFile faceImage) throws IOException;
}