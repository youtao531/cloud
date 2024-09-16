package com.seeta.proxy;

import com.seeta.pool.PoseEstimatorPool;
import com.seeta.pool.SeetaConfSetting;
import com.seeta.sdk.PoseEstimator;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaRect;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PoseEstimatorProxy {

    private final PoseEstimatorPool pool;

    public PoseEstimatorProxy(SeetaConfSetting confSetting) {
        pool = new PoseEstimatorPool(confSetting);
    }

    public PoseItem estimate(SeetaImageData image, SeetaRect face) {
        float[] yaw = new float[1];
        float[] pitch = new float[1];
        float[] roll = new float[1];

        PoseEstimator poseEstimator = null;

        try {
            poseEstimator = pool.borrowObject();
            poseEstimator.Estimate(image, face, yaw, pitch, roll);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (poseEstimator != null) {
                pool.returnObject(poseEstimator);
            }
        }
        return new PoseItem(yaw[0], pitch[0], roll[0]);
    }

    @Setter
    @Getter
    public static class PoseItem {
        private float yaw;
        private float pitch;
        private float roll;

        public PoseItem(float yaw, float pitch, float roll) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }
    }
}
