package com.org.linkedin.service.impl;

import com.org.linkedin.model.Experience;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ExperienceRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.ExperienceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    public ExperienceServiceImpl(ExperienceRepository experienceRepository, UserRepository userRepository) {
        this.experienceRepository = experienceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Experience> getAllExperiencesByUserId(Long userId) {
        return experienceRepository.findByUserUserId(userId);
    }

    @Override
    public Experience addExperience(Long userId, Experience experience) {
        User user = userRepository.findById(userId).orElseThrow();
        experience.setUser(user);
        return experienceRepository.save(experience);
    }

    @Override
    public Experience updateExperience(Long experienceId, Experience updatedExperience) {
        Experience experience = experienceRepository.findById(experienceId).orElseThrow();
        experience.setCompanyName(updatedExperience.getCompanyName());
        experience.setTitle(updatedExperience.getTitle());
        experience.setLocation(updatedExperience.getLocation());
        experience.setStartDate(updatedExperience.getStartDate());
        experience.setEndDate(updatedExperience.getEndDate());
        experience.setDescription(updatedExperience.getDescription());
        return experienceRepository.save(experience);
    }

    @Override
    public void deleteExperience(Long experienceId) {
        experienceRepository.deleteById(experienceId);
    }

    @Override
    public Experience getExperienceById(Long id) {
        return experienceRepository.findById(id).orElse(null);
    }
}
