package com.framework.cloud.application.impl;

import com.framework.cloud.application.*;
import com.framework.cloud.domain.model.basic.ComCodes;
import com.framework.cloud.domain.repository.RegisterFaceRepository;
import com.framework.cloud.infrastructure.exception.constant.ErrorException;
import com.framework.cloud.interfaces.vo.FaceModel;
import com.framework.cloud.interfaces.vo.FaceModelScore;
import com.seeta.proxy.*;
import com.seeta.sdk.FaceAntiSpoofing;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;
import com.seeta.sdk.util.SeetafaceUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.date.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

/**
 * seetaface6SDK API
 */
@Slf4j
@Service
public class SeetafaceServiceImpl implements FaceAgeService, FaceGenderService, FaceLiveService, FaceMaskService, FaceQualityService, FaceSimilarityService, FaceRegisterService, FaceSearchService {

    @Resource
    private FaceDetectorProxy faceDetector;
    @Resource
    private FaceLandmarkerProxy faceLandmarker;
    @Resource
    private FaceRecognizerProxy faceRecognizer;
    @Resource
    private GenderPredictorProxy genderPredictor;
    @Resource
    private AgePredictorProxy agePredictor;
    @Resource
    private MaskDetectorProxy maskDetector;
    @Resource
    private FaceAntiSpoofingProxy faceAntiSpoofing;
    @Resource
    private QualityOfLBNProxy qualityOfLBN;
    @Resource
    private RegisterFaceRepository registerFaceRepository;

    @Override
    public FaceAntiSpoofing.Status faceAntiSpoofing(MultipartFile faceImage) {
        FaceAntiSpoofing.Status predict;
        try {
            DetectResult detectResult = getDetectResult(faceImage);

            SeetaPointF[] pointFS = faceLandmarker.mark(detectResult.image, detectResult.detect);
            predict = faceAntiSpoofing.predict(detectResult.image, detectResult.detect, pointFS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return predict;
    }

    @Override
    public MaskDetectorProxy.MaskItem maskDetector(MultipartFile faceImage) {
        MaskDetectorProxy.MaskItem detect;
        try {
            DetectResult detectResult = getDetectResult(faceImage);

            detect = maskDetector.detect(detectResult.image, detectResult.detect);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return detect;
    }

    @Override
    public GenderPredictorProxy.GenderItem genderPredictor(MultipartFile faceImage) {
        GenderPredictorProxy.GenderItem gender;
        try {
            DetectResult detectResult = getDetectResult(faceImage);

            SeetaPointF[] pointFS = faceLandmarker.mark(detectResult.image, detectResult.detect);
            gender = genderPredictor.predictGenderWithCrop(detectResult.image, pointFS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return gender;
    }

    @Override
    public Integer agePredictor(MultipartFile faceImage) {
        int age;
        try {
            DetectResult detectResult = getDetectResult(faceImage);

            SeetaPointF[] pointFS = faceLandmarker.mark(detectResult.image, detectResult.detect);
            age = agePredictor.predictAgeWithCrop(detectResult.image, pointFS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return age;
    }

    @Override
    public Float faceRecognizer(MultipartFile face1, MultipartFile face2) {
        float calculateSimilarity = 0.00F;
        try {
            DetectResult detectResult1 = getDetectResult(face1);
            DetectResult detectResult2 = getDetectResult(face2);

            float[] features1 = faceRecognizer.extract(detectResult1.image, faceLandmarker.mark(detectResult1.image, detectResult1.detect));
            float[] features2 = faceRecognizer.extract(detectResult2.image, faceLandmarker.mark(detectResult2.image, detectResult2.detect));

            if (features1 != null && features2 != null) {
                calculateSimilarity = faceRecognizer.cosineSimilarity(features1, features2);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return calculateSimilarity;
    }

    @Override
    public QualityOfLBNProxy.LBNClass faceQuality(MultipartFile faceImage) {
        QualityOfLBNProxy.LBNClass result;
        try {
            DetectResult detectResult = getDetectResult(faceImage);

            SeetaPointF[] pointFS = faceLandmarker.mark(detectResult.image, detectResult.detect);
            result = qualityOfLBN.detect(detectResult.image, pointFS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public String registerFace(MultipartFile faceImage) {
        String faceId;
        try {
            DetectResult detectResult = getDetectResult(faceImage);

            String imgName = faceImage.getOriginalFilename();
            SeetaPointF[] pointFS = faceLandmarker.mark(detectResult.image, detectResult.detect);
            float[] features = faceRecognizer.extract(detectResult.image, pointFS);
            //一个人脸的基本信息
            FaceModel faceModel = new FaceModel();
            faceModel.setFeatures(features);
            faceModel.setCreateTime(DateUtil.formatNow());
            faceModel.setFileName(imgName);
            faceModel.setSeetaRect(detectResult.detect);
            faceModel.setPointFS(pointFS);

            faceId = registerFaceRepository.register(faceModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return faceId;
    }

    @Override
    public FaceModelScore faceSearch(MultipartFile faceImage) {
        FaceModelScore res;
        try {
            DetectResult detectResult = getDetectResult(faceImage);

            SeetaPointF[] pointFS = faceLandmarker.mark(detectResult.image(), detectResult.detect());
            float[] features = faceRecognizer.extract(detectResult.image(), pointFS);
            res = registerFaceRepository.search(features);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    private DetectResult getDetectResult(MultipartFile faceImage) throws Exception {
        SeetaImageData image = SeetafaceUtil.toSeetaImageData(ImageIO.read(faceImage.getInputStream()));
        SeetaRect[] detects = faceDetector.detect(image);
        if (ArrayUtil.isEmpty(detects)) {
            throw new ErrorException(ComCodes.COMMON_FAIL.getCode(), "NOT_DETECT_FACE");
        }
        return new DetectResult(image, detects[0]);
    }

    private record DetectResult(SeetaImageData image, SeetaRect detect) {
    }
}