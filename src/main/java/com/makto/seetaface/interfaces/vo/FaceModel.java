package com.makto.seetaface.interfaces.vo;

import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;
import lombok.Data;
import lombok.ToString;

/**
 * 单个人脸数据
 */
@Data
@ToString
public class FaceModel {

    /**
     * 注册时生成的Id
     */
    private String id;

    /**
     * 注册时图片文件名
     */
    private String fileName;

    /**
     * 注册时备注的内容
     */
    private String content;

    /**
     * 注册时间
     */
    private String createTime;

    /**
     * 人脸位置
     */
    private SeetaRect seetaRect;

    /**
     * 人脸关键点
     */
    private SeetaPointF[] pointFS;

    /**
     * 人脸向量特征数组
     */
    private float[] features;
}
