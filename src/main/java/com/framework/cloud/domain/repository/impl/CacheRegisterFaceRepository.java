package com.framework.cloud.domain.repository.impl;

import com.framework.cloud.domain.repository.RegisterFaceRepository;
import com.framework.cloud.infrastructure.properties.FaceProperties;
import com.framework.cloud.interfaces.vo.FaceModel;
import com.framework.cloud.interfaces.vo.FaceModelScore;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.data.id.IdUtil;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 通过缓存注册与搜索人脸
 *
 * @author youtao531 on 2023/10/7 19:23
 */
@Slf4j
public class CacheRegisterFaceRepository implements RegisterFaceRepository {

    static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Map<String, FaceModel> faceCache = new HashMap<>();
    private final FaceProperties faceProperties;

    public CacheRegisterFaceRepository(FaceProperties faceProperties) {
        this.faceProperties = faceProperties;
    }

    @Override
    public List<String> register(List<FaceModel> faceModels) {
        List<String> ids = new ArrayList<>();
        if (!faceModels.isEmpty()) {
            for (FaceModel faceModel : faceModels) {
                String id = register(faceModel);
                ids.add(id);
            }
        }
        return ids;
    }

    @Override
    public String register(FaceModel faceModel) {
        FaceProperties.RegisterConfig registerConfig = faceProperties.getRegisterConfig();
        Integer maxFace = registerConfig.getMaxFace();

        //向量数据 长度不等于512 或是1024的不允许注册
        if (faceModel.getFeatures() != null && (faceModel.getFeatures().length == 512 || faceModel.getFeatures().length == 1024)) {
            String id = null;
            if (faceCache.size() < maxFace) {
                lock.writeLock().lock();
                try {
                    if (faceCache.size() < maxFace) {
                        //根据文件名和向量特征生成ID
                        id = IdUtil.objectId();
                        faceModel.setId(id);
                        faceCache.put(id, faceModel);
                    } else {
                        log.warn("缓存数据已满，不能再注册人脸了！ {}", faceCache.size());
                    }
                    return id;
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                log.warn("缓存数据已满，不能再注册人脸了！ {}", faceCache.size());
            }
        }
        log.warn("注册的faceModel对象数据不规范！ {}", faceModel);
        return null;
    }

    @Override
    public List<FaceModelScore> search(float[] features, int topN, float minimum_score) {
        FaceProperties.RegisterConfig registerConfig = faceProperties.getRegisterConfig();
        Float score = registerConfig.getScore();

        List<FaceModelScore> list = new ArrayList<>();
        if (minimum_score <= 0.0F) {
            if (score != null && score > 0.0F) {
                minimum_score = score;
            } else {
                minimum_score = 0.68F;
            }
        }
        lock.readLock().lock();
        try {
            for (Map.Entry<String, FaceModel> next : faceCache.entrySet()) {
                FaceModel faceModel = next.getValue();
                float scoreTmp = cosineSimilarity(features, faceModel.getFeatures());
                if (scoreTmp >= minimum_score) {
                    FaceModelScore faceModelScore = new FaceModelScore();
                    BeanUtils.copyProperties(faceModel, faceModelScore);
                    faceModelScore.setScore(scoreTmp);
                    list.add(faceModelScore);
                }
            }
        } finally {
            lock.readLock().unlock();
        }

        List<FaceModelScore> collect = list.stream()
                .sorted(Comparator.comparing(FaceModelScore::getScore).reversed())
                .collect(Collectors.toList());

        if (collect.size() > topN) {
            return collect.subList(0, topN);
        }
        return collect;
    }

    @Override
    public FaceModelScore search(float[] features) {
        FaceProperties.RegisterConfig registerConfig = faceProperties.getRegisterConfig();
        Integer topN = registerConfig.getTopN();
        Float score = registerConfig.getScore();

        int topNLimit;
        float minimum_score;
        if (topN == null || topN <= 0) {
            topNLimit = 20;
        } else {
            topNLimit = topN;
        }
        if (score == null || score <= 0) {
            minimum_score = 0.68F;
        } else {
            minimum_score = score;
        }
        List<FaceModelScore> search = search(features, topNLimit, minimum_score);

        return search.getFirst();
    }

    public float cosineSimilarity(float[] leftVector, float[] rightVector) {
        double dotProduct = 0.0;

        for (int i = 0; i < leftVector.length; ++i) {
            dotProduct += leftVector[i] * rightVector[i];
        }

        double d1 = 0.0;
        for (float value : leftVector) {
            d1 += Math.pow(value, 2.0);
        }

        double d2 = 0.0;
        for (float value : rightVector) {
            d2 += Math.pow(value, 2.0);
        }

        double cosineSimilarity;
        if (!(d1 <= 0.0) && !(d2 <= 0.0)) {
            cosineSimilarity = dotProduct / (Math.sqrt(d1) * Math.sqrt(d2));
        } else {
            cosineSimilarity = 0.0;
        }

        return (float) cosineSimilarity;
    }
}
