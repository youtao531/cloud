package com.makto.seetaface.infrastructure.sdk.paddle;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.makto.seetaface.infrastructure.sdk.paddle.model.OcrBlock;
import com.makto.seetaface.infrastructure.sdk.paddle.model.PaddleOCRResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author Lcc 2023/10/14 11:43
 */
@Slf4j
public final class PaddleOCRHelper {

    /**
     * 调用OCR服务
     *
     * @param apiUrl      服务地址
     * @param imageBase64 图片流转base64
     * @param headerMap   请求标头
     * @return 识别结果
     */
    public static PaddleOCRResult paddleOcr(String apiUrl, String imageBase64, Map<String, String> headerMap) {
        Map<String, Object> map = MapUtil.newHashMap();
        map.put("key", new String[]{"image"});
        map.put("value", new String[]{imageBase64});

        String body;
        try (HttpResponse response = HttpRequest
                .post(apiUrl)
                .body(JSONUtil.toJsonStr(map), ContentType.JSON.getValue())
                .addHeaders(headerMap)
                .timeout(8_000)
                .execute()) {
            body = response.body();
        }
        log.info("------> {} -> PaddleOCR ==> {}", apiUrl, body);

        return JSONUtil.toBean(body, PaddleOCRResult.class);
    }

    /**
     * 解析OCR远程调用的结果
     *
     * @param ocrResult OCR原始结果
     * @return 解析后结果
     */
    public static List<OcrBlock> parseOcrResult(PaddleOCRResult ocrResult) {
        List<String> value = ocrResult.getValue();
        String string = value.get(0);
        string = string
                .replaceAll("\\('", "\"")
                .replaceAll("\\(\"", "\"")
                .replaceAll("',", "\",")
                .replaceAll("\\),", ",")
                .replaceAll("'", "");
        JSONArray array = JSONUtil.parseArray(string);

        return array.stream()
                .map(itemObj -> {
                    JSONArray itemArray = JSONUtil.parseArray(itemObj);
                    String text = Convert.toStr(itemArray.get(0));
                    Double rate = Convert.toDouble(itemArray.get(1));
                    JSONArray pointList = (JSONArray) itemArray.get(2);

                    List<OcrBlock.Point> points = pointList.stream()
                            .map(pointObj -> {
                                JSONArray pointArray = JSONUtil.parseArray(pointObj);
                                Double x = Convert.toDouble(pointArray.get(0));
                                Double y = Convert.toDouble(pointArray.get(1));
                                return new OcrBlock.Point(x, y);
                            })
                            .toList();
                    return new OcrBlock(text, rate, points);
                })
                .toList();
    }

}