package com.org.linkedin.service;

import com.org.linkedin.model.Certification;

import java.util.List;

public interface CertificationService {
    List<Certification> getCertificationsByUserId(Long userId);
    Certification getCertificationById(Long Id);
    Certification saveCertification(Certification certification);
    void deleteCertification(Long certificationId);
}