package com.seeta.proxy;

import com.seeta.pool.QualityOfLBNPool;
import com.seeta.pool.SeetaConfSetting;
import com.seeta.sdk.QualityOfLBN;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QualityOfLBNProxy {

    private QualityOfLBNPool pool;

    private QualityOfLBNProxy() {
    }

    public QualityOfLBNProxy(SeetaConfSetting setting) {
        pool = new QualityOfLBNPool(setting);
    }

    public LBNClass detect(SeetaImageData imageData, SeetaPointF[] points) {
        int[] light = new int[1];
        int[] blur = new int[1];
        int[] noise = new int[1];

        QualityOfLBN qualityOfLBN = null;
        try {
            qualityOfLBN = pool.borrowObject();
            qualityOfLBN.Detect(imageData, points, light, blur, noise);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (qualityOfLBN != null) {
                pool.returnObject(qualityOfLBN);
            }
        }

        return new LBNClass(light, blur, noise);
    }

    @Getter
    @Setter
    public static class LBNClass {
        private QualityOfLBN.LIGHTSTATE lightstate;
        private QualityOfLBN.BLURSTATE blurstate;
        private QualityOfLBN.NOISESTATE noisestate;

        public LBNClass(int[] light, int[] blur, int[] noise) {
            this.lightstate = QualityOfLBN.LIGHTSTATE.values()[light[0]];
            this.blurstate = QualityOfLBN.BLURSTATE.values()[blur[0]];
            this.noisestate = QualityOfLBN.NOISESTATE.values()[noise[0]];
        }
    }
}
