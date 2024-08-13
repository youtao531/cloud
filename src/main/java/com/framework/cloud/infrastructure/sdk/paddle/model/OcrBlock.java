package com.framework.cloud.infrastructure.sdk.paddle.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author youtao531 2023/10/14 13:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "OcrBlock", title = "OCR区块信息", description = "OCR区块信息")
public class OcrBlock implements Serializable {

    @Schema(title = "识别出的文本", requiredMode = Schema.RequiredMode.REQUIRED)
    private String text;

    @Schema(title = "可信度", description = "识别正确的可信度(格式：小数形式表示)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double reliability;

    @Schema(title = "坐标点", description = "区块范围坐标点(4点表示)", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Point> points;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "Point", title = "OCR区块坐标", description = "OCR区块坐标")
    public static class Point implements Serializable {

        @Schema(title = "X轴", requiredMode = Schema.RequiredMode.REQUIRED)
        private Double x;

        @Schema(title = "Y轴", requiredMode = Schema.RequiredMode.REQUIRED)
        private Double y;
    }
}