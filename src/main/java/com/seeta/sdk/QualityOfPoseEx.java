package com.seeta.sdk;

import lombok.Getter;

/**
 * 深度学习的人脸姿态评估器。
 */
public class QualityOfPoseEx {

    public long impl = 0;

    /**
     * 人脸姿态评估器构造函数。
     *
     * @param setting setting
     */
    public QualityOfPoseEx(SeetaModelSetting setting) throws Exception {
        this.construct(setting);
    }

    /**
     * 人脸姿态评估器构造函数
     */
    private native void construct(SeetaModelSetting setting) throws Exception;

    /**
     * 检测人脸姿态
     *
     * @param imageData [input]image data
     * @param face      [input] face location
     * @param landmarks [input] face landmarks
     * @param score     [output] quality score
     * @return QualityLevel quality level sorted into "LOW" , "Medium" , "HIGH"
     */
    private native int checkCore(SeetaImageData imageData, SeetaRect face, SeetaPointF[] landmarks, float[] score);

    /**
     * 检测人脸姿态
     */
    public QualityLevel check(SeetaImageData imageData, SeetaRect face, SeetaPointF[] landmarks, float[] score) {
        int index = this.checkCore(imageData, face, landmarks, score);

        return QualityLevel.values()[index];
    }

    /**
     * 检测人脸姿态
     *
     * @param imageData [input]image data
     * @param face      [input] face location
     * @param landmarks [input] face landmarks
     * @param yaw       [output] face location in yaw  偏航中的面部位置
     * @param pitch     [output] face location in pitch 俯仰中的面部位置
     * @param roll      [oputput] face location in roll  面卷中的位置
     */
    private native void checkCore(SeetaImageData imageData, SeetaRect face, SeetaPointF[] landmarks, float[] yaw, float[] pitch, float[] roll);

    /**
     * 检测人脸姿态
     *
     * @param yaw   偏航中的面部位置
     * @param pitch 俯仰中的面部位置
     * @param roll  面卷中的位置
     */
    public void check(SeetaImageData imageData, SeetaRect face, SeetaPointF[] landmarks, float[] yaw, float[] pitch, float[] roll) {
        this.checkCore(imageData, face, landmarks, yaw, pitch, roll);
    }

    public native void set(Property property, double value);

    public native double get(Property property);

    public enum QualityLevel {
        LOW,//Quality level is low
        MEDIUM,//Quality level is medium
        HIGH,//Quality level is high
    }

    @Getter
    public enum Property {
        YAW_LOW_THRESHOLD(0),
        YAW_HIGH_THRESHOLD(1),
        PITCH_LOW_THRESHOLD(2),
        PITCH_HIGH_THRESHOLD(3),
        ROLL_LOW_THRESHOLD(4),
        ROLL_HIGH_THRESHOLD(5);

        private final int value;

        Property(int value) {
            this.value = value;
        }
    }
}
