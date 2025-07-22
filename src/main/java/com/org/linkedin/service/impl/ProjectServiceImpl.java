package com.org.linkedin.service.impl;

import com.org.linkedin.model.Project;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ProjectRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Project addProject(Long userId, Project project) {
        Optional<User> user = userRepository.findById(userId);
        project.setUser(user.get());
        return projectRepository.save(project);
    }

    @Override
    public List<Project> getProjectsByUserId(Long userId) {
        return projectRepository.findByUserUserId(userId);
    }

    @Override
    public void deleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    @Override
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElse(null);
    }

    @Override
    public Project updateProject(Long projectId, Project updated) {
        Project existing = projectRepository.findById(projectId).orElseThrow();
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setSkills(updated.getSkills());
        return projectRepository.save(existing);
    }
}
