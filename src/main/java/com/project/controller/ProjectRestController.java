package com.project.controller;

import com.project.model.Project;
import com.project.service.ProjectService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class ProjectRestController {
    private ProjectService projectService;

    @Autowired
    public ProjectRestController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/projects/{projectId}")
    ResponseEntity<Project> getProject(@PathVariable Integer projectId) {
        return ResponseEntity.of(projectService.getProject(projectId));
    }

    @GetMapping(value = "/projects")
    Page<Project> getProjects(Pageable pageable) {
        return projectService.getProjects(pageable);
    }

    @GetMapping(value = "/projects", params="name")
    Page<Project> getProjectsByName(@RequestParam String name, Pageable pageable) {
        return projectService.searchByName(name, pageable);
    }

    @PostMapping(path = "/projects")
    ResponseEntity<Void> createProject(@Valid @RequestBody Project project) {
        Project createdProject = projectService.setProject(project);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{projectId}").buildAndExpand(createdProject.getProjectId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/projects/{projectId}")
    public ResponseEntity<Void> updateProject(@Valid @RequestBody Project project, @PathVariable Integer projectId) {
        return projectService.getProject(projectId)
                .map(p -> {
                    updateProjectFields(p, project);
                    projectService.setProject(p);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private void updateProjectFields(Project persistedProject, Project updatedProject) {
        persistedProject.setName(updatedProject.getName());
        persistedProject.setDescription(updatedProject.getDescription());
        persistedProject.setHandinDate(updatedProject.getHandinDate());
    }

    @PutMapping("/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<Void> addTaskToProject(@PathVariable Integer projectId, @PathVariable Integer taskId) {
        return projectService.getProject(projectId)
                .map(p -> {
                    try {
                        projectService.addTaskToProject(p, taskId);
                    }
                    catch(ObjectNotFoundException e) {
                        return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                    }
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Integer projectId) {
        return projectService.getProject(projectId)
            .map(p -> {
                projectService.deleteProject(projectId);
                return new ResponseEntity<Void>(HttpStatus.OK);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
