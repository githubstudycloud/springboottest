package com.study.collect.business.medical.service;

import com.study.collect.business.medical.collector.MedicalCollector;
import com.study.collect.business.medical.model.MedicalData;
import com.study.collect.business.medical.repository.MedicalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicalService {

    private final MedicalCollector collector;
    private final MedicalRepository repository;

    public MedicalData collectPatientData(String patientId) {
        // 1. 采集和处理数据
        MedicalData data = collector.collect(patientId);

        // 2. 保存数据
        return repository.save(data);
    }
}