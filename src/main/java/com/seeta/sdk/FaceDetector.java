package com.seeta.sdk;


import lombok.Getter;

/**
 * 人脸检测器 检测到的每个人脸位置，用矩形表示。
 *
 * @author youtao531
 */
public class FaceDetector {

    public long impl = 0;

    public FaceDetector(SeetaModelSetting setting) throws Exception {
        this.construct(setting);
    }

    private native void construct(SeetaModelSetting setting) throws Exception;

    public native void dispose();

    public native SeetaRect[] Detect(SeetaImageData image);

    public native void set(Property property, double value);

    public native double get(Property property);

    @Getter
    public enum Property {
        PROPERTY_MIN_FACE_SIZE(0),
        PROPERTY_THRESHOLD(1),
        PROPERTY_MAX_IMAGE_WIDTH(2),
        PROPERTY_MAX_IMAGE_HEIGHT(3),
        PROPERTY_NUMBER_THREADS(4),
        PROPERTY_ARM_CPU_MODE(0x101);

        private final int value;

        Property(int value) {
            this.value = value;
        }
    }
}
