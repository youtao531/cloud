package com.makto.seetaface.application;

import com.seeta.sdk.FaceAntiSpoofing;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 人脸活体检测
 *
 * @author Lcc 2023/9/25 11:16
 */
public interface FaceLiveService {

    /**
     * 攻击人脸检测
     *
     * @param faceImage 人脸图片文件
     * @return FaceAntiSpoofing.Status
     */
    FaceAntiSpoofing.Status faceAntiSpoofing(MultipartFile faceImage);
}
