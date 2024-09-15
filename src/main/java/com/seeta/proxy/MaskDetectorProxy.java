package com.seeta.proxy;

import com.seeta.pool.MaskDetectorPool;
import com.seeta.pool.SeetaConfSetting;
import com.seeta.sdk.MaskDetector;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaRect;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MaskDetectorProxy {

    private MaskDetectorPool pool;

    private MaskDetectorProxy() {
    }

    public MaskDetectorProxy(SeetaConfSetting confSetting) {

        pool = new MaskDetectorPool(confSetting);
    }

    public MaskItem detect(SeetaImageData imageData, SeetaRect face) {
        float[] score = new float[1];
        boolean detect = false;
        MaskDetector maskDetector = null;
        try {
            maskDetector = pool.borrowObject();
            detect = maskDetector.detect(imageData, face, score);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (maskDetector != null) {
                pool.returnObject(maskDetector);
            }
        }

        return new MaskItem(detect, score[0]);
    }


    @Setter
    public static class MaskItem {
        @Getter
        private float score;

        private boolean mask;

        public MaskItem(boolean mask, float score) {
            this.mask = mask;
            this.score = score;
        }

        public boolean getMask() {
            return mask;
        }
    }

}
