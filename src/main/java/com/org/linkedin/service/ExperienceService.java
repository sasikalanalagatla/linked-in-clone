package com.org.linkedin.service;

import com.org.linkedin.model.Experience;

import java.util.List;

public interface ExperienceService {
    List<Experience> getAllExperiencesByUserId(Long userId);
    Experience addExperience(Long userId, Experience experience);
    Experience updateExperience(Long experienceId, Experience updatedExperience);
    void deleteExperience(Long experienceId);
    Experience getExperienceById(Long id);
}
