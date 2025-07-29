package com.org.linkedin.service;

import com.org.linkedin.model.Education;
import com.org.linkedin.service.UserService;
import org.springframework.ui.Model;

import java.util.List;

public interface EducationService {
    Education addEducation(Long userId, Education education);
    Education updateEducation(Long educationId, Education updatedEducation);
    void deleteEducation(Long educationId);
    Education getEducationById(Long id);

    String showEducationForm(Long userId, Model model, UserService userService);
    String addEducation(Long userId, Education education, Model model, UserService userService);
    String showEditEducationForm(Long educationId, Model model, UserService userService);
    String updateEducation(Long educationId, Education education, Model model, UserService userService);
    String deleteEducation(Long educationId, Model model);
}