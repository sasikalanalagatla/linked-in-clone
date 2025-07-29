package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Education;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.EducationRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.EducationService;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

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
    public Education addEducation(Long userId, Education education) {
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
        return educationRepository.save(education);
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
    public Education getEducationById(Long id) {
        if (id == null) {
            throw new CustomException("INVALID_EDUCATION_ID", "Education ID cannot be null");
        }
        return educationRepository.findById(id)
                .orElseThrow(() -> new CustomException("EDUCATION_NOT_FOUND", "Education with ID " + id + " not found"));
    }

    public String showEducationForm(Long userId, Model model, UserService userService) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }

        User user = userService.getUserById(userId);

        model.addAttribute("user", user);
        model.addAttribute("education", new Education());

        return "add-education";
    }

    public String addEducation(Long userId, Education education, Model model, UserService userService) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }

        if (education == null) {
            throw new CustomException("INVALID_EDUCATION", "Education data cannot be null");
        }

        try {
            addEducation(userId, education);
            return "redirect:/profile/" + userId;
        } catch (CustomException e) {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            model.addAttribute("education", education);
            model.addAttribute("error", "Error saving education: " + e.getMessage());
            return "add-education";
        }
    }

    public String showEditEducationForm(Long educationId, Model model, UserService userService) {
        if (educationId == null) {
            throw new CustomException("INVALID_EDUCATION_ID", "Education ID cannot be null");
        }

        Education education = getEducationById(educationId);
        User user = userService.getUserById(education.getUser().getUserId());

        model.addAttribute("education", education);
        model.addAttribute("user", user);

        return "add-education";
    }

    public String updateEducation(Long educationId, Education education, Model model, UserService userService) {
        if (educationId == null) {
            throw new CustomException("INVALID_EDUCATION_ID", "Education ID cannot be null");
        }

        if (education == null) {
            throw new CustomException("INVALID_EDUCATION", "Education data cannot be null");
        }

        try {
            Education updatedEducation = updateEducation(educationId, education);
            return "redirect:/profile/" + updatedEducation.getUser().getUserId();
        } catch (CustomException e) {
            User user = userService.getUserById(education.getUser().getUserId());
            model.addAttribute("user", user);
            model.addAttribute("education", education);
            model.addAttribute("error", "Error updating education: " + e.getMessage());
            return "add-education";
        }
    }

    public String deleteEducation(Long educationId, Model model) {
        if (educationId == null) {
            throw new CustomException("INVALID_EDUCATION_ID", "Education ID cannot be null");
        }

        try {
            Education education = getEducationById(educationId);
            Long userId = education.getUser().getUserId();
            deleteEducation(educationId);
            return "redirect:/profile/" + userId;
        } catch (CustomException e) {
            model.addAttribute("error", "Error deleting education: " + e.getMessage());
            return "error";
        }
    }
}
