package com.org.linkedin.service.impl;

import com.org.linkedin.model.Education;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.EducationRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.EducationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final UserRepository userRepository;

    public EducationServiceImpl(EducationRepository educationRepository, UserRepository userRepository) {
        this.educationRepository = educationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Education> getAllEducationsByUserId(Long userId) {
        return educationRepository.findByUserUserId(userId);
    }

    @Override
    public Education addEducation(Long userId, Education education) {
        User user = userRepository.findById(userId).orElseThrow();
        education.setUser(user);
        return educationRepository.save(education);
    }

    @Override
    public Education updateEducation(Long educationId, Education updatedEducation) {
        Education education = educationRepository.findById(educationId).orElseThrow();
        education.setSchoolName(updatedEducation.getSchoolName());
        education.setDegree(updatedEducation.getDegree());
        education.setFieldOfStudy(updatedEducation.getFieldOfStudy());
        education.setStartDate(updatedEducation.getStartDate());
        education.setEndDate(updatedEducation.getEndDate());
        education.setGrade(updatedEducation.getGrade());
        education.setExtraCurricularActivity(updatedEducation.getExtraCurricularActivity());
        return educationRepository.save(education);
    }

    public void deleteEducation(Long educationId) {
        educationRepository.deleteById(educationId);
    }

    public void saveOrUpdate(Long userId, Education education) {
        User user = userRepository.findById(userId).orElseThrow();
        education.setUser(user);
        educationRepository.save(education);
    }

    public Education getEducationById(Long id) {
        return educationRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        educationRepository.deleteById(id);
    }

}
