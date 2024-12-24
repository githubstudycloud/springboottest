package com.study.collect.business.medical.processor;

import com.study.collect.business.medical.model.MedicalData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ImageProcessor {

    public MedicalData process(MedicalData data) {
        if (data.getImageData() != null) {
            // 图像增强
            enhanceImage(data);

            // 图像压缩
            compressImage(data);
        }
        return data;
    }

    private void enhanceImage(MedicalData data) {
        // 图像增强处理逻辑
        log.info("Enhancing image for patient: {}", data.getPatientId());
    }

    private void compressImage(MedicalData data) {
        // 图像压缩处理逻辑
        log.info("Compressing image for patient: {}", data.getPatientId());
    }
}
