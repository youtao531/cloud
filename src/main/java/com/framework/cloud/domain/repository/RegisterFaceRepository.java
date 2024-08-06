package com.framework.cloud.domain.repository;

import com.framework.cloud.interfaces.vo.FaceModel;
import com.framework.cloud.interfaces.vo.FaceModelScore;

import java.util.List;

/**
 * 注册人脸的抽象方法
 */
public interface RegisterFaceRepository {

    List<String> register(List<FaceModel> faceModels);

    String register(FaceModel faceModel);

    List<FaceModelScore> search(float[] features, int topN, float minimum_score);

    FaceModelScore search(float[] features);
}