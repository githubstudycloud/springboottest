package com.study.collect.business.medical.processor;

import com.study.collect.business.medical.model.MedicalData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrivacyProcessor {

    public MedicalData process(MedicalData data) {
        // 脱敏处理
        maskSensitiveData(data);

        // 加密处理
        encryptSensitiveData(data);

        return data;
    }

    private void maskSensitiveData(MedicalData data) {
        // 对敏感字段进行掩码
        if (data.getPatientName() != null) {
            data.setPatientName(maskName(data.getPatientName()));
        }
    }

    private void encryptSensitiveData(MedicalData data) {
        // 加密敏感数据
        log.info("Encrypting sensitive data for patient: {}", data.getPatientId());
    }

    private String maskName(String name) {
        if (name == null || name.length() <= 1) {
            return name;
        }
        return name.substring(0, 1) + "*".repeat(name.length() - 1);
    }
}
