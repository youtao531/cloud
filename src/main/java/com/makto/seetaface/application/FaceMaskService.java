package com.makto.seetaface.application;

import com.seeta.proxy.MaskDetectorProxy;
import org.springframework.web.multipart.MultipartFile;

/**
 * 人脸口罩检测
 *
 * @author Lcc 2023/9/25 11:16
 */
public interface FaceMaskService {

    /**
     * 带口罩人脸检测
     *
     * @param faceImage 人脸图片文件
     * @return 人是否带口罩
     */
    MaskDetectorProxy.MaskItem maskDetector(MultipartFile faceImage);
}
