package com.framework.cloud.infrastructure.properties;

import com.framework.cloud.domain.model.RegisterMethod;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Seetaface6配置
 *
 * @author youtao531 2023/5/20 14:57
 */
@Data
@Component
@ConfigurationProperties(prefix = "seetaface6")
public class FaceProperties {

    /**
     * 高精度
     */
    private Boolean highQuality = true;
    /**
     * 人脸注册配置
     */
    private RegisterConfig registerConfig;
    /**
     * 模型文件配置
     */
    private ModelConfig modelConfig;

    /**
     * 人脸注册配置
     */
    @Data
    @Component
    @ConfigurationProperties(prefix = "register-config")
    public static class RegisterConfig {
        /**
         * 人脸注册方式
         */
        private RegisterMethod method;
        /**
         * 能够注册的最大人脸数
         */
        private Integer maxFace;
        /**
         * 查询前面多少
         */
        private Integer topN;
        /**
         * 查询的最小分数
         */
        private Float score;
    }

    /**
     * 模型文件配置
     */
    @Data
    @Component
    @ConfigurationProperties(prefix = "model-config")
    public static class ModelConfig {
        /**
         * 模型文件基础路径
         */
        private String base;

        /**
         * 人脸检测器，用矩形表示检测到的每个人脸位置
         */
        private String[] faceDetector;
        /**
         * 人脸标识模型，5点确定 两眼、嘴角和鼻尖(SeetaPointF[] 即 x，y坐标数组)
         */
        private String[] faceLandMarkerPts5;
        /**
         * 人脸标识模型，人脸68个特征点(SeetaPointF[] 即 x，y坐标数组)
         */
        private String[] faceLandMarkerPts68;
        /**
         * 遮挡评估，判断的遮挡物为五个关键点，分别是左眼中心、右眼中心、左嘴角、右嘴角和鼻尖(0-没遮挡，1-遮挡)
         */
        private String[] faceLandMarkerMaskPts5;
        /**
         * 高精度人脸识别人脸向量特征提取模型(返回1024长度向量特征)，建议阈值：0.62
         */
        private String[] faceRecognizer;
        /**
         * 轻量级人脸向量特征提取模型(返回512长度向量特征)，建议阈值：0.55
         */
        private String[] faceRecognizerLight;
        /**
         * 戴口罩人脸向量特征提取模型(返回512长度向量特征)，建议阈值：0.48
         */
        private String[] faceRecognizerMask;
        /**
         * 年龄预测模型(返回int[0])
         */
        private String[] agePredictor;
        /**
         * 眼睛状态评估(打开、关闭)
         */
        private String[] eyeState;
        /**
         * 性别识别
         */
        private String[] genderPredictor;
        /**
         * 口罩检测器(false-0.0089 或 true-0.985)
         */
        private String[] maskDetector;
        /**
         * 人脸姿态评估
         */
        private String[] poseEstimation;
        /**
         * 清晰度评估
         */
        private String[] qualityLbn;
        /**
         * 活体检测识别器(局部检测模型)
         */
        private String fasFirst;
        /**
         * 活体检测识别器(全局检测模型)
         */
        private String fasSecond;
    }
}