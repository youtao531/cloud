package com.makto.seetaface.infrastructure.sdk.paddle.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Lcc 2023/10/14 16:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockInfo implements Serializable {

    @Schema(title = "字段名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(title = "识别出的文本", requiredMode = Schema.RequiredMode.REQUIRED)
    private String text;

    @Schema(title = "可信度", description = "识别正确的可信度(格式：小数形式表示)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double reliability;
}