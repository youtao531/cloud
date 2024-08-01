package com.makto.seetaface.interfaces.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FaceModelScore extends FaceModel {

    private Float score;
}