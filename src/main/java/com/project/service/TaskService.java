package com.project.service;

import com.project.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface TaskService {
    Optional<Task> getTask(Integer taskId);
    Task setTask(Task task);
    void deleteTask(Integer taskId);
    Page<Task> getTasks(Pageable pageable);
    List<Task> findTasksByProjectId(Integer projectId);
    Page<Task> findTasksByProjectId(Integer projectId, Pageable pageable);
}
