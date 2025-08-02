package com.org.linkedin.repository;

import com.org.linkedin.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
}