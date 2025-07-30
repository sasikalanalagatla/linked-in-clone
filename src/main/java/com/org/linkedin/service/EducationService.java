package com.org.linkedin.service;

import com.org.linkedin.model.Education;

public interface EducationService {
    Education addEducation(Long userId, Education education);
    Education updateEducation(Long educationId, Education updatedEducation);
    void deleteEducation(Long educationId);
    Education getEducationById(Long educationId);
}