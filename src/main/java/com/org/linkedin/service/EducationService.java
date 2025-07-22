package com.org.linkedin.service;

import com.org.linkedin.model.Education;

import java.util.List;

public interface EducationService {
    List<Education> getAllEducationsByUserId(Long userId);
    Education addEducation(Long userId, Education education);
    Education updateEducation(Long educationId, Education updatedEducation);
    void deleteEducation(Long educationId);
    Education getEducationById(Long id);
    void saveOrUpdate(Long userId, Education education);
}