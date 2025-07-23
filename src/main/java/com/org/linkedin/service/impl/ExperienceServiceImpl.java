package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Experience;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ExperienceRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.ExperienceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        return experienceRepository.findByUserUserId(userId);
    }

    @Override
    public Experience addExperience(Long userId, Experience experience) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        if (experience == null) {
            throw new CustomException("INVALID_EXPERIENCE", "Experience data cannot be null");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new CustomException("USER_NOT_FOUND", "User with ID " + userId + " not found");
        }
        experience.setUser(user.get());
        return experienceRepository.save(experience);
    }

    @Override
    public Experience updateExperience(Long experienceId, Experience updatedExperience) {
        if (experienceId == null) {
            throw new CustomException("INVALID_EXPERIENCE_ID", "Experience ID cannot be null");
        }
        if (updatedExperience == null) {
            throw new CustomException("INVALID_EXPERIENCE", "Experience data cannot be null");
        }
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new CustomException("EXPERIENCE_NOT_FOUND", "Experience with ID " + experienceId + " not found"));
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
        if (experienceId == null) {
            throw new CustomException("INVALID_EXPERIENCE_ID", "Experience ID cannot be null");
        }
        if (!experienceRepository.existsById(experienceId)) {
            throw new CustomException("EXPERIENCE_NOT_FOUND", "Experience with ID " + experienceId + " not found");
        }
        experienceRepository.deleteById(experienceId);
    }

    @Override
    public Experience getExperienceById(Long id) {
        if (id == null) {
            throw new CustomException("INVALID_EXPERIENCE_ID", "Experience ID cannot be null");
        }
        return experienceRepository.findById(id)
                .orElseThrow(() -> new CustomException("EXPERIENCE_NOT_FOUND", "Experience with ID " + id + " not found"));
    }
}
