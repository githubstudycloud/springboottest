package com.study.collect.business.medical.engine;

import com.study.collect.business.medical.model.MedicalData;
import com.study.collect.core.collector.model.CollectResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MedicalEngine {

    private final com.study.collect.business.medical.processor.DicomProcessor dicomProcessor;
    private final com.study.collect.business.medical.processor.ImageProcessor imageProcessor;
    private final com.study.collect.business.medical.processor.PrivacyProcessor privacyProcessor;

    public CollectResult<MedicalData> process(String patientId) {
        // 1. 读取DICOM文件
        MedicalData data = dicomProcessor.readDicomData(patientId);

        // 2. 处理图像数据
        data = imageProcessor.process(data);

        // 3. 隐私数据处理
        data = privacyProcessor.process(data);
        CollectResult<MedicalData> result = new CollectResult<>();
        result.setData(data);
        return result;
    }
}