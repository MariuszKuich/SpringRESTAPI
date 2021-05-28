package com.project.repository;

import com.project.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query("SELECT z FROM Task z WHERE z.project.projectId = :projectId")
    Page<Task> findTasksByProjectId(@Param("projectId") Integer projectId, Pageable pageable);

    @Query("SELECT z FROM Task z WHERE z.project.projectId = :projectId")
    List<Task> findTasksByProjectId(@Param("projectId") Integer projectId);
}
