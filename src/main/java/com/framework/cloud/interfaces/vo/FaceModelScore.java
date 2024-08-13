package com.framework.cloud.interfaces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FaceModelScore extends FaceModel {

    private Float score;
}