package com.seeta.proxy;

import com.seeta.pool.AgePredictorPool;
import com.seeta.pool.SeetaConfSetting;
import com.seeta.sdk.AgePredictor;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import lombok.extern.slf4j.Slf4j;

/**
 * 年龄评估器
 */
@Slf4j
public class AgePredictorProxy {

    private AgePredictorPool pool;

    private AgePredictorProxy() {
    }


    public AgePredictorProxy(SeetaConfSetting config) {
        pool = new AgePredictorPool(config);
    }

    public int predictAgeWithCrop(SeetaImageData image, SeetaPointF[] points) {

        AgePredictor agePredictor = null;

        int[] age = new int[1];

        try {
            agePredictor = pool.borrowObject();
            agePredictor.PredictAgeWithCrop(image, points, age);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (agePredictor != null) {
                pool.returnObject(agePredictor);
            }
        }
        return age[0];
    }
}
