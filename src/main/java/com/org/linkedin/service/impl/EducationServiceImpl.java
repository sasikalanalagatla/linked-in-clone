package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Education;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.EducationRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.EducationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final UserRepository userRepository;

    public EducationServiceImpl(EducationRepository educationRepository, UserRepository userRepository) {
        this.educationRepository = educationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addEducation(Long userId, Education education) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        if (education == null) {
            throw new CustomException("INVALID_EDUCATION", "Education data cannot be null");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new CustomException("USER_NOT_FOUND", "User with ID " + userId + " not found");
        }
        education.setUser(user.get());
        educationRepository.save(education);
    }

    @Override
    public Education updateEducation(Long educationId, Education updatedEducation) {
        if (educationId == null) {
            throw new CustomException("INVALID_EDUCATION_ID", "Education ID cannot be null");
        }
        if (updatedEducation == null) {
            throw new CustomException("INVALID_EDUCATION", "Education data cannot be null");
        }
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new CustomException("EDUCATION_NOT_FOUND", "Education with ID " + educationId + " not found"));
        education.setSchoolName(updatedEducation.getSchoolName());
        education.setDegree(updatedEducation.getDegree());
        education.setFieldOfStudy(updatedEducation.getFieldOfStudy());
        education.setStartDate(updatedEducation.getStartDate());
        education.setEndDate(updatedEducation.getEndDate());
        education.setGrade(updatedEducation.getGrade());
        education.setExtraCurricularActivity(updatedEducation.getExtraCurricularActivity());
        return educationRepository.save(education);
    }

    @Override
    public void deleteEducation(Long educationId) {
        if (educationId == null) {
            throw new CustomException("INVALID_EDUCATION_ID", "Education ID cannot be null");
        }
        if (!educationRepository.existsById(educationId)) {
            throw new CustomException("EDUCATION_NOT_FOUND", "Education with ID " + educationId + " not found");
        }
        educationRepository.deleteById(educationId);
    }

    @Override
    public Education getEducationById(Long educationId) {
        if (educationId == null) {
            throw new CustomException("INVALID_EDUCATION_ID", "Education ID cannot be null");
        }
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new CustomException("EDUCATION_NOT_FOUND", "Education with ID " + educationId + " not found"));
    }
}