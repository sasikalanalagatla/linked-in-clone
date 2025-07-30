package com.org.linkedin.service;

import com.org.linkedin.model.Certification;

public interface CertificationService {

    Certification getCertificationById(Long Id);
    Certification saveCertification(Certification certification);
    void deleteCertification(Long certificationId);
}