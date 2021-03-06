package com.project.service;

import com.project.model.Project;
import com.project.model.Task;
import com.project.repository.ProjectRepository;
import com.project.repository.TaskRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProjectServiceImpl implements ProjectService {
    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public Optional<Project> getProject(Integer projectId) {
        return projectRepository.findById(projectId);
    }

    @Override
    public Project setProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteProject(Integer projectId) {
        for(Task task : taskRepository.findTasksByProjectId(projectId)) {
            taskRepository.delete(task);
        }
        projectRepository.deleteById(projectId);
    }

    @Override
    public Page<Project> getProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    @Override
    public Page<Project> searchByName(String name, Pageable pageable) {
        return projectRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public void addTaskToProject(Project project, Integer taskId) throws ObjectNotFoundException {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if(optionalTask.isPresent()) {
            Task task = optionalTask.get();

            task.setProject(project);
            taskRepository.save(task);

            project.setTasks(Stream.concat(project.getTasks().stream(), Stream.of(task)).collect(Collectors.toList()));
            setProject(project);
        }
        else {
            throw new ObjectNotFoundException(taskId, "task");
        }
    }
}
