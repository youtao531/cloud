package com.seeta.proxy;

import com.seeta.pool.QualityOfBrightnessPool;
import com.seeta.pool.SeetaConfSetting;
import com.seeta.sdk.QualityOfBrightness;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QualityOfBrightnessProxy {

    private final QualityOfBrightnessPool pool;

    public QualityOfBrightnessProxy() {
        pool = new QualityOfBrightnessPool(new SeetaConfSetting());
    }


    public QualityOfBrightnessProxy(SeetaConfSetting confSetting) {
        pool = new QualityOfBrightnessPool(confSetting);
    }

    public BrightnessItem check(SeetaImageData imageData, SeetaRect face, SeetaPointF[] landmarks) {
        float[] score = new float[1];
        QualityOfBrightness.QualityLevel check = null;
        QualityOfBrightness qualityOfBrightness = null;

        try {
            qualityOfBrightness = pool.borrowObject();
            check = qualityOfBrightness.check(imageData, face, landmarks, score);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (qualityOfBrightness != null) {
                pool.returnObject(qualityOfBrightness);
            }
        }
        return new BrightnessItem(check, score[0]);
    }

    @Setter
    @Getter
    public static class BrightnessItem {
        private QualityOfBrightness.QualityLevel check;
        private float score;

        public BrightnessItem(QualityOfBrightness.QualityLevel check, float score) {
            this.check = check;
            this.score = score;
        }
    }
}
