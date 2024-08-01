package com.makto.seetaface.infrastructure.sdk.paddle.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Lcc 2023/10/27 14:24
 */
@Data
public class BlockMap implements Serializable {

    private BlockInfo id;
    private BlockInfo gender;
    private BlockInfo fullName;
    private BlockInfo dateOfBirth;
}
