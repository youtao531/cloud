package com.seeta.sdk;

import lombok.Getter;
import lombok.Setter;

/**
 * 判断人脸遮挡
 */
@Setter
@Getter
public class LandmarkerMask {

    private SeetaPointF[] seetaPointFS;
    private int[] masks;
}
