package com.framework.cloud.domain.repository.impl;

import com.framework.cloud.domain.repository.RegisterFaceRepository;
import com.framework.cloud.infrastructure.properties.FaceProperties;
import com.framework.cloud.interfaces.vo.FaceModel;
import com.framework.cloud.interfaces.vo.FaceModelScore;

import java.util.List;

/**
 * TODO 通过数据库注册与搜索人脸
 *
 * @author youtao531 2023/10/7 19:23
 */
public class MySQLRegisterFaceRepository implements RegisterFaceRepository {

    private final FaceProperties faceProperties;

    public MySQLRegisterFaceRepository(FaceProperties faceProperties) {
        this.faceProperties = faceProperties;
    }

    @Override
    public List<String> register(List<FaceModel> faceModels) {
        return null;
    }

    @Override
    public String register(FaceModel faceModel) {
        return null;
    }

    @Override
    public List<FaceModelScore> search(float[] features, int topN, float minimum_score) {
        return null;
    }

    @Override
    public FaceModelScore search(float[] features) {
        return null;
    }
}
