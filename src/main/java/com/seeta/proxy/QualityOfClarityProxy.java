package com.seeta.proxy;

import com.seeta.pool.QualityOfClarityPool;
import com.seeta.pool.SeetaConfSetting;
import com.seeta.sdk.QualityOfClarity;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QualityOfClarityProxy {

    private final QualityOfClarityPool pool;

    public QualityOfClarityProxy() {
        pool = new QualityOfClarityPool(new SeetaConfSetting());
    }

    public QualityOfClarityProxy(SeetaConfSetting setting) {
        pool = new QualityOfClarityPool(setting);
    }

    public ClarityItem check(SeetaImageData imageData, SeetaRect face, SeetaPointF[] landmarks) {
        float[] score = new float[1];
        QualityOfClarity qualityOfClarity = null;
        QualityOfClarity.QualityLevel check = null;
        try {
            qualityOfClarity = pool.borrowObject();
            check = qualityOfClarity.check(imageData, face, landmarks, score);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (qualityOfClarity != null) {
                pool.returnObject(qualityOfClarity);
            }
        }
        return new ClarityItem(check, score[0]);
    }

    @Setter
    @Getter
    public static class ClarityItem {
        private QualityOfClarity.QualityLevel qualityLevel;
        private float score;

        public ClarityItem(QualityOfClarity.QualityLevel qualityLevel, float score) {
            this.qualityLevel = qualityLevel;
            this.score = score;
        }
    }
}
