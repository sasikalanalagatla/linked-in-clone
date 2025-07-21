package com.org.linkedin.service;

import com.org.linkedin.model.Education;

import java.util.List;

public interface EducationService {
    List<Education> getAllEducationsByUserId(Long userId);
    Education addEducation(Long userId, Education education);
    Education updateEducation(Long educationId, Education education);
    void deleteEducation(Long educationId);
}
