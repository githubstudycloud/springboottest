package com.study.business.medical.engine;

import com.study.business.medical.model.MedicalData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MedicalEngine {

    private final DicomProcessor dicomProcessor;
    private final ImageProcessor imageProcessor;
    private final PrivacyProcessor privacyProcessor;

    public MedicalData process(String patientId) {
        // 1. 读取DICOM文件
        MedicalData data = dicomProcessor.readDicomData(patientId);

        // 2. 处理图像数据
        data = imageProcessor.process(data);

        // 3. 隐私数据处理
        data = privacyProcessor.process(data);

        return data;
    }
}