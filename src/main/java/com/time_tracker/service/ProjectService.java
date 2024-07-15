package com.time_tracker.service;

import com.time_tracker.model.Project;
import com.time_tracker.repositories.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

}