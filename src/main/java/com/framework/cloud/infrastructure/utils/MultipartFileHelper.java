package com.framework.cloud.infrastructure.utils;

import com.framework.cloud.domain.core.ComCodes;
import com.framework.cloud.infrastructure.exception.constant.ErrorException;
import org.dromara.hutool.core.map.MapUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

/**
 * @author youtao531 on 2024/8/15 10:55
 */
public final class MultipartFileHelper {

    /**
     * 解析上传文件信息
     */
    public static Map<String, Object> imageInfo(MultipartFile file) {
        Map<String, Object> map = MapUtil.newHashMap();
        map.put("filename", file.getOriginalFilename());
        map.put("filetype", file.getContentType());
        map.put("size", file.getSize());
        return map;
    }

    public static byte[] toBytes(MultipartFile file) {
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new ErrorException(ComCodes.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
        }
        return bytes;
    }

    public static String toBytesBase64(MultipartFile file) {
        byte[] bytes = toBytes(file);
        return toBytesBase64(bytes);
    }

    public static String toBytesBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
