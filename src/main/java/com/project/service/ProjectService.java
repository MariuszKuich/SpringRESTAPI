package com.project.service;

import com.project.model.Project;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ProjectService {
    Optional<Project> getProject(Integer projectId);
    Project setProject(Project project);
    void deleteProject(Integer projectId);
    Page<Project> getProjects(Pageable pageable);
    Page<Project> searchByName(String name, Pageable pageable);
    void addTaskToProject(Project project, Integer taskId) throws ObjectNotFoundException;
}
