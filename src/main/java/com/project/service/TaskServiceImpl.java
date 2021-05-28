package com.project.service;

import com.project.model.Task;
import com.project.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {
    private TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Optional<Task> getTask(Integer taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public Task setTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Integer taskId) {
        taskRepository.deleteById(taskId);
    }

    @Override
    public Page<Task> getTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    public List<Task> findTasksByProjectId(Integer projectId) {
        return taskRepository.findTasksByProjectId(projectId);
    }

    @Override
    public Page<Task> findTasksByProjectId(Integer projectId, Pageable pageable) {
        return taskRepository.findTasksByProjectId(projectId, pageable);
    }
}
