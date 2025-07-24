package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Project;
import com.org.linkedin.model.Skill;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ProjectRepository;
import com.org.linkedin.repository.SkillRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, SkillRepository skillRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveProject(Long userId, Project project, String skillsString, BindingResult result) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        if (project == null) {
            throw new CustomException("INVALID_PROJECT", "Project data cannot be null");
        }
        if (skillsString == null || skillsString.trim().isEmpty()) {
            result.rejectValue("skills", "skills.empty", "Skills cannot be empty");
            throw new CustomException("INVALID_SKILLS", "Skills cannot be empty");
        }

        String[] skillNamesArray = skillsString.split(",");
        List<String> skillNames = new ArrayList<>();
        for (String skillName : skillNamesArray) {
            String trimmed = skillName.trim();
            if (!trimmed.isEmpty()) {
                skillNames.add(trimmed);
            }
        }

        if (skillNames.isEmpty()) {
            result.rejectValue("skills", "skills.invalid", "At least one valid skill is required");
            throw new CustomException("INVALID_SKILLS", "At least one valid skill is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User with ID " + userId + " not found"));
        project.setUser(user);

        projectRepository.save(project);

        List<Skill> skills = new ArrayList<>();
        for (String name : skillNames) {
            Skill skill = skillRepository.findBySkillNameIgnoreCase(name)
                    .orElseGet(() -> {
                        Skill newSkill = new Skill();
                        newSkill.setSkillName(name);
                        return skillRepository.save(newSkill);
                    });
            skills.add(skill);
        }

        project.setSkills(skills);
        projectRepository.save(project);
    }

    @Override
    public void updateProject(Long userId, Long projectId, Project project, String skillsString, BindingResult result) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        if (projectId == null) {
            throw new CustomException("INVALID_PROJECT_ID", "Project ID cannot be null");
        }
        if (project == null) {
            throw new CustomException("INVALID_PROJECT", "Project data cannot be null");
        }

        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException("PROJECT_NOT_FOUND", "Project with ID " + projectId + " not found"));

        if (!existingProject.getUser().getUserId().equals(userId)) {
            throw new CustomException("UNAUTHORIZED_ACCESS", "Unauthorized access to project");
        }

        if (skillsString == null || skillsString.trim().isEmpty()) {
            result.rejectValue("skills", "skills.empty", "Skills cannot be empty");
            throw new CustomException("INVALID_SKILLS", "Skills cannot be empty");
        }

        String[] skillNamesArray = skillsString.split(",");
        List<String> skillNames = new ArrayList<>();
        for (String skillName : skillNamesArray) {
            String trimmed = skillName.trim();
            if (!trimmed.isEmpty()) {
                skillNames.add(trimmed);
            }
        }

        if (skillNames.isEmpty()) {
            result.rejectValue("skills", "skills.invalid", "At least one valid skill is required");
            throw new CustomException("INVALID_SKILLS", "At least one valid skill is required");
        }

        existingProject.setName(project.getName());
        existingProject.setDescription(project.getDescription());

        List<Skill> skills = new ArrayList<>();
        for (String name : skillNames) {
            Skill skill = skillRepository.findBySkillNameIgnoreCase(name)
                    .orElseGet(() -> {
                        Skill newSkill = new Skill();
                        newSkill.setSkillName(name);
                        return skillRepository.save(newSkill);
                    });
            skills.add(skill);
        }

        existingProject.setSkills(skills);
        projectRepository.save(existingProject);
    }

    @Override
    public Project getProjectById(Long projectId) {
        if (projectId == null) {
            throw new CustomException("INVALID_PROJECT_ID", "Project ID cannot be null");
        }
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException("PROJECT_NOT_FOUND", "Project with ID " + projectId + " not found"));
    }

    @Override
    public String getSkillsStringForProject(Long projectId) {
        if (projectId == null) {
            throw new CustomException("INVALID_PROJECT_ID", "Project ID cannot be null");
        }
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException("PROJECT_NOT_FOUND", "Project with ID " + projectId + " not found"));
        List<Skill> skills = project.getSkills();
        StringBuilder skillsString = new StringBuilder();
        for (int i = 0; i < skills.size(); i++) {
            skillsString.append(skills.get(i).getSkillName());
            if (i < skills.size() - 1) {
                skillsString.append(", ");
            }
        }
        return skillsString.toString();
    }

    @Override
    public void deleteProject(Long projectId) {
        if (projectId == null) {
            throw new CustomException("INVALID_PROJECT_ID", "Project ID cannot be null");
        }
        if (!projectRepository.existsById(projectId)) {
            throw new CustomException("PROJECT_NOT_FOUND", "Project with ID " + projectId + " not found");
        }
        projectRepository.deleteById(projectId);
    }
}