package com.makto.seetaface.application;

import com.makto.seetaface.interfaces.vo.FaceModelScore;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 人脸搜索
 *
 * @author Lcc 2023/9/25 11:16
 */
public interface FaceSearchService {

    /**
     * 人脸搜索
     *
     * @param faceImage 人脸图片文件
     * @return 人脸搜索
     */
    FaceModelScore faceSearch(MultipartFile faceImage) throws IOException;
}