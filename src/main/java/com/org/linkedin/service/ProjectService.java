package com.org.linkedin.service;

import com.org.linkedin.model.Project;

import java.util.List;

public interface ProjectService {
    Project addProject(Long userId, Project project);
    List<Project> getProjectsByUserId(Long userId);
    void deleteProject(Long projectId);
    Project getProjectById(Long projectId);
    Project updateProject(Long projectId, Project project);
}
