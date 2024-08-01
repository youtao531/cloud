package com.makto.seetaface.domain.repository.impl;

import com.makto.seetaface.domain.repository.RegisterFaceRepository;
import com.makto.seetaface.infrastructure.properties.FaceProperties;
import com.makto.seetaface.interfaces.vo.FaceModel;
import com.makto.seetaface.interfaces.vo.FaceModelScore;

import java.util.List;

/**
 * TODO 通过数据库注册与搜索人脸
 *
 * @author Lcc 2023/10/7 19:23
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
