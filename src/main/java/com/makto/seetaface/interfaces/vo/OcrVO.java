package com.makto.seetaface.interfaces.vo;

import com.makto.seetaface.domain.model.CardType;
import com.makto.seetaface.infrastructure.sdk.paddle.model.BlockMap;
import com.makto.seetaface.infrastructure.sdk.paddle.model.OcrBlock;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author Lcc 2023/10/14 14:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(name = "OcrVO", title = "OCR信息", description = "OCR信息")
public class OcrVO implements Serializable {

    @Schema(title = "执行耗时", description = "OCR远程调用耗时(单位：毫秒)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long execMillis;

    @Schema(title = "国家代码", description = "国家代码ISO(格式：2位字符，例如：TZ,GH,CI)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String countryCode;

    @Schema(title = "卡片类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private CardType cardType;

    @Schema(title = "区块数量", description = "OCR可识别出的总区块数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer blockSize;

    @Schema(title = "区块", description = "处理过的区块信息", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BlockMap blockMap;

    @Schema(title = "区块", description = "原始区块信息", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<OcrBlock> blocks;
}