package com.study.collect.business.medical.processor;

import com.study.collect.business.medical.model.MedicalData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DicomProcessor {

    public MedicalData readDicomData(String patientId) {
        // 模拟读取DICOM文件
        log.info("Reading DICOM data for patient: {}", patientId);

        MedicalData data = new MedicalData();
        data.setPatientId(patientId);
        // 设置其他数据...
        return data;
    }
}