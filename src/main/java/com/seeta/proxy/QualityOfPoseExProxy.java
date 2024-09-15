package com.seeta.proxy;

import com.seeta.pool.QualityOfPoseExPool;
import com.seeta.pool.SeetaConfSetting;
import com.seeta.sdk.QualityOfPoseEx;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QualityOfPoseExProxy {

    private QualityOfPoseExPool pool;

    private QualityOfPoseExProxy() {
    }

    public QualityOfPoseExProxy(SeetaConfSetting setting) {
        pool = new QualityOfPoseExPool(setting);
    }

    /**
     * 检测人脸姿态
     *
     * @param imageData 图片
     * @param face      人脸图片
     */
    public QualityOfPoseEx.QualityLevel check(SeetaImageData imageData, SeetaRect face, SeetaPointF[] landmarks) {
        QualityOfPoseEx.QualityLevel qualityLevel = null;
        QualityOfPoseEx qualityOfPoseEx = null;
        float[] scores = new float[1];
        try {
            qualityOfPoseEx = pool.borrowObject();
            qualityLevel = qualityOfPoseEx.check(imageData, face, landmarks, scores);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (qualityOfPoseEx != null) {
                pool.returnObject(qualityOfPoseEx);
            }
        }
        return qualityLevel;
    }

    /**
     * 检测人脸姿态
     *
     * @param imageData [input]image data
     * @param face      [input] face location
     * @param landmarks [input] face landmarks
     * @return yaw       [output] face location in yaw  偏航中的面部位置
     */
    public PoseExItem checkCore(SeetaImageData imageData, SeetaRect face, SeetaPointF[] landmarks) {
        float[] yaw = new float[1];
        float[] pitch = new float[1];
        float[] roll = new float[1];

        QualityOfPoseEx qualityOfPoseEx = null;
        try {
            qualityOfPoseEx = pool.borrowObject();
            qualityOfPoseEx.check(imageData, face, landmarks, yaw, pitch, roll);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (qualityOfPoseEx != null) {
                pool.returnObject(qualityOfPoseEx);
            }
        }
        return new PoseExItem(yaw[0], pitch[0], roll[0]);
    }

    @Setter
    @Getter
    public static class PoseExItem {
        private float yaw;
        private float pitch;
        private float roll;

        public PoseExItem(float yaw, float pitch, float roll) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }
    }
}
