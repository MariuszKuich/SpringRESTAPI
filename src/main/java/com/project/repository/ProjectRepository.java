package com.project.repository;

import com.project.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<Project> findByNameContainingIgnoreCase(String name);
}
