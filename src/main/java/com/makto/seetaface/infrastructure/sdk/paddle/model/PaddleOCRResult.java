package com.makto.seetaface.infrastructure.sdk.paddle.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Lcc 2023/10/14 11:33
 */
@Data
public class PaddleOCRResult implements Serializable {

    private Integer err_no;
    private String err_msg;
    private List<String> key;
    private List<String> value;
    private List<String> tensors;

    public boolean ok() {
        return this.err_no == 0;
    }

    public boolean fail() {
        return !ok();
    }
}