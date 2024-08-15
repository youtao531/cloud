package com.framework.cloud.infrastructure.configuration;

import com.framework.cloud.domain.model.RegisterMethod;
import com.framework.cloud.domain.repository.RegisterFaceRepository;
import com.framework.cloud.domain.repository.impl.CacheRegisterFaceRepository;
import com.framework.cloud.domain.repository.impl.MySQLRegisterFaceRepository;
import com.framework.cloud.infrastructure.properties.FaceProperties;
import com.seeta.pool.SeetaConfSetting;
import com.seeta.proxy.*;
import com.seeta.sdk.SeetaDevice;
import com.seeta.sdk.SeetaModelSetting;
import com.seeta.sdk.util.LoadNativeCore;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * seetaface6识别器 连接池代理配置
 *
 * @author youtao531 on 2022/12/10 18:25
 */
@Slf4j
@Configuration
public class Seetaface6Config {

    @Resource
    private FaceProperties faceProperties;

    @PostConstruct
    public void init() {
        if (isProduction()) {
            //加载dll
            LoadNativeCore.LOAD_NATIVE(SeetaDevice.SEETA_DEVICE_AUTO);
        }
    }

    private SeetaConfSetting buildSeetaConfSetting(String[] models) throws FileNotFoundException {
        return new SeetaConfSetting(new SeetaModelSetting(0, models, SeetaDevice.SEETA_DEVICE_AUTO, isProduction()));
    }

    private boolean isProduction() {
        String os = System.getProperty("os.name");
        return !os.contains("Mac");
    }

    /**
     * 人脸注册服务
     *
     * @return RegisterFace
     */
    @Bean
    public RegisterFaceRepository registerFaceRepository() {
        RegisterMethod method = faceProperties.getRegisterConfig().getMethod();
        return method == RegisterMethod.cache
                ? new CacheRegisterFaceRepository(faceProperties) : new MySQLRegisterFaceRepository(faceProperties);
    }

    /**
     * 人脸检测器
     */
    @Bean
    public FaceDetectorProxy faceDetector() throws FileNotFoundException {
        FaceProperties.ModelConfig modelConfig = faceProperties.getModelConfig();
        String[] faceDetector = modelConfig.getFaceDetector();
        log.debug("人脸识别检测器，模型文件路径： {}", Arrays.toString(faceDetector));

        return new FaceDetectorProxy(buildSeetaConfSetting(faceDetector));
    }

    /**
     * 人脸关键点定位器(5点或68点定位)
     */
    @Bean
    public FaceLandmarkerProxy faceLandmarker() throws FileNotFoundException {
        FaceProperties.ModelConfig modelConfig = faceProperties.getModelConfig();
        Boolean highQuality = faceProperties.getHighQuality();
        String[] faceLandMarkerPts = highQuality ? modelConfig.getFaceLandMarkerPts68() : modelConfig.getFaceLandMarkerPts5();
        log.debug("人脸特征定位器，模型文件路径： {}", Arrays.toString(faceLandMarkerPts));

        return new FaceLandmarkerProxy(buildSeetaConfSetting(faceLandMarkerPts));
    }

    /**
     * 人脸特征提取人脸比对器
     */
    @Bean
    public FaceRecognizerProxy faceRecognizer() throws FileNotFoundException {
        FaceProperties.ModelConfig modelConfig = faceProperties.getModelConfig();
        String[] faceRecognizer = modelConfig.getFaceRecognizer();
        log.debug("人脸特征提取器，模型文件路径： {}", Arrays.toString(faceRecognizer));

        return new FaceRecognizerProxy(buildSeetaConfSetting(faceRecognizer));
    }

    /**
     * 人脸性别识别器
     */
    @Bean
    public GenderPredictorProxy genderPredictor() throws FileNotFoundException {
        FaceProperties.ModelConfig modelConfig = faceProperties.getModelConfig();
        String[] genderPredictor = modelConfig.getGenderPredictor();
        log.debug("人脸性别识别器，模型文件路径： {}", Arrays.toString(genderPredictor));

        return new GenderPredictorProxy(buildSeetaConfSetting(genderPredictor));
    }

    /**
     * 人脸年龄检测器
     */
    @Bean
    public AgePredictorProxy agePredictor() throws FileNotFoundException {
        FaceProperties.ModelConfig modelConfig = faceProperties.getModelConfig();
        String[] agePredictor = modelConfig.getAgePredictor();
        log.debug("人脸年龄检测器，模型文件路径： {}", Arrays.toString(agePredictor));

        return new AgePredictorProxy(buildSeetaConfSetting(agePredictor));
    }

    /**
     * 人脸口罩检测器
     */
    @Bean
    public MaskDetectorProxy maskDetector() throws FileNotFoundException {
        FaceProperties.ModelConfig modelConfig = faceProperties.getModelConfig();
        String[] maskDetector = modelConfig.getMaskDetector();
        log.debug("人脸口罩检测器，模型文件路径： {}", Arrays.toString(maskDetector));

        return new MaskDetectorProxy(buildSeetaConfSetting(maskDetector));
    }

    /**
     * 人脸攻击检测器
     */
    @Bean
    public FaceAntiSpoofingProxy faceAntiSpoofing() throws FileNotFoundException {
        FaceProperties.ModelConfig modelConfig = faceProperties.getModelConfig();
        String fasFirst = modelConfig.getFasFirst();
        String fasSecond = modelConfig.getFasSecond();
        log.debug("人脸攻击检测器，模型文件路径：模型一 {} ，模型二 {}", fasFirst, fasSecond);

        String[] fasArray = {fasFirst, fasSecond};
        return new FaceAntiSpoofingProxy(buildSeetaConfSetting(fasArray));
    }

    /**
     * 人脸质量检测器
     */
    @Bean
    public QualityOfLBNProxy qualityOfLBN() throws FileNotFoundException {
        FaceProperties.ModelConfig modelConfig = faceProperties.getModelConfig();
        String[] qualityLbn = modelConfig.getQualityLbn();
        log.debug("人脸质量检测器，模型文件路径： {}", Arrays.toString(qualityLbn));

        return new QualityOfLBNProxy(buildSeetaConfSetting(qualityLbn));
    }
}