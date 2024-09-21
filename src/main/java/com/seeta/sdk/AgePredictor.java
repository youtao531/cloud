package com.seeta.sdk;

import lombok.Getter;

/**
 * 年龄估计器
 *
 * @author youtao531
 */
public class AgePredictor {

    public long impl = 0;

    public AgePredictor(SeetaModelSetting setting) {
        this.construct(setting);
    }

    private native void construct(SeetaModelSetting setting);

    public native void dispose();

    public native int GetCropFaceWidth();

    public native int GetCropFaceHeight();

    public native int GetCropFaceChannels();

    public native boolean CropFace(SeetaImageData image, SeetaPointF[] points, SeetaImageData face);

    public native boolean PredictAge(SeetaImageData face, int[] age);

    public native boolean PredictAgeWithCrop(SeetaImageData image, SeetaPointF[] points, int[] age);

    /**
     * 获取照片的年龄评估
     *
     * @param image  SeetaImageData
     * @param points points
     * @return 将接口重写，使其符合java代码正常写法
     */
    public Integer predictAgeWithCrop(SeetaImageData image, SeetaPointF[] points) {
        int[] ages = new int[1];
        this.PredictAgeWithCrop(image, points, ages);

        return ages[0];
    }

    public native void set(Property property, double value);

    public native double get(Property property);

    @Getter
    public enum Property {
        PROPERTY_NUMBER_THREADS(4),
        PROPERTY_ARM_CPU_MODE(5);

        private final int value;

        Property(int value) {
            this.value = value;
        }
    }
}
