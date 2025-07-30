package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Certification;
import com.org.linkedin.repository.CertificationRepository;
import com.org.linkedin.service.CertificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;

    public CertificationServiceImpl(CertificationRepository certificationRepository) {
        this.certificationRepository = certificationRepository;
    }

    @Override
    public Certification getCertificationById(Long certificationId) {
        if (certificationId == null) {
            throw new CustomException("INVALID_CERTIFICATION_ID", "Certification ID cannot be null");
        }
        return certificationRepository.findById(certificationId)
                .orElseThrow(() -> new CustomException("CERTIFICATION_NOT_FOUND", "Certification with ID " + certificationId + " not found"));
    }

    @Override
    public Certification saveCertification(Certification certification) {
        if (certification == null) {
            throw new CustomException("INVALID_CERTIFICATION", "Certification data cannot be null");
        }
        return certificationRepository.save(certification);
    }

    @Override
    public void deleteCertification(Long certificationId) {
        if (certificationId == null) {
            throw new CustomException("INVALID_CERTIFICATION_ID", "Certification ID cannot be null");
        }
        if (!certificationRepository.existsById(certificationId)) {
            throw new CustomException("CERTIFICATION_NOT_FOUND", "Certification with ID " + certificationId + " not found");
        }
        certificationRepository.deleteById(certificationId);
    }
}