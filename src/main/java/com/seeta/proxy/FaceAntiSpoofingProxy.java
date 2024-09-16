package com.seeta.proxy;

import com.seeta.pool.FaceAntiSpoofingPool;
import com.seeta.pool.SeetaConfSetting;
import com.seeta.sdk.FaceAntiSpoofing;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;
import lombok.extern.slf4j.Slf4j;

/**
 * 人脸反欺诈
 */
@Slf4j
public class FaceAntiSpoofingProxy {

    private FaceAntiSpoofingPool pool;

    private FaceAntiSpoofingProxy() {
    }

    public FaceAntiSpoofingProxy(SeetaConfSetting setting) {
        pool = new FaceAntiSpoofingPool(setting);
    }

    public FaceAntiSpoofing.Status predict(SeetaImageData image, SeetaRect face, SeetaPointF[] landmarks) {

        FaceAntiSpoofing.Status status = null;
        FaceAntiSpoofing faceAntiSpoofing = null;
        try {

            faceAntiSpoofing = pool.borrowObject();
            status = faceAntiSpoofing.Predict(image, face, landmarks);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (faceAntiSpoofing != null) {
                pool.returnObject(faceAntiSpoofing);
            }
        }

        return status;
    }
}
