package com.project.controller;

import com.project.model.Task;
import com.project.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api")
public class TaskRestController {
    private TaskService taskService;

    @Autowired
    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks/{taskId}")
    ResponseEntity<Task> getTask(@PathVariable Integer taskId) {
        return ResponseEntity.of(taskService.getTask(taskId));
    }

    @PostMapping(path = "/tasks")
    ResponseEntity<Void> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.setTask(task);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{taskId}").buildAndExpand(createdTask.getTaskId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<Void> updateTask(@Valid @RequestBody Task task, @PathVariable Integer taskId) {
        return taskService.getTask(taskId)
                .map(p -> {
                    taskService.setTask(task);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer taskId) {
        return taskService.getTask(taskId).map(p -> {
            taskService.deleteTask(taskId);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/tasks")
    Page<Task> getTasks(Pageable pageable) {
        return taskService.getTasks(pageable);
    }

    @GetMapping(value = "/tasks", params="projectId")
    Page<Task> getTasksByProjectId(@RequestParam Integer projectId, Pageable pageable) {
        return taskService.findTasksByProjectId(projectId, pageable);
    }
}
