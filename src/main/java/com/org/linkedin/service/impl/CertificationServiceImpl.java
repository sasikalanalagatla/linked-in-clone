package com.org.linkedin.service.impl;

import com.org.linkedin.model.Certification;
import com.org.linkedin.repository.CertificationRepository;
import com.org.linkedin.service.CertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificationServiceImpl implements CertificationService {

    @Autowired
    private CertificationRepository certificationRepository;

    @Override
    public List<Certification> getCertificationsByUserId(Long userId) {
        return certificationRepository.findByUserUserId(userId);
    }

    @Override
    public Certification getCertificationById(Long certificationId) {
        return certificationRepository.findById(certificationId)
                .orElseThrow(() -> new RuntimeException("Certification not found"));
    }

    @Override
    public Certification saveCertification(Certification certification) {
        return certificationRepository.save(certification);
    }

    @Override
    public void deleteCertification(Long certificationId) {
        certificationRepository.deleteById(certificationId);
    }
}