package com.study.business.medical.repository;


import com.study.business.medical.model.MedicalData;
import com.study.collect.core.storage.repository.IRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface medicalRepository extends IRepository<MedicalData, String>, MongoRepository<MedicalData, String> {
    MedicalData findByCode(String code);
}
