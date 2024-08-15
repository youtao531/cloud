package com.framework.cloud.interfaces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 人脸分数
 *
 * @author youtao531 on 2024/8/15 10:55
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FaceModelScore extends FaceModel {

    private Float score;
}