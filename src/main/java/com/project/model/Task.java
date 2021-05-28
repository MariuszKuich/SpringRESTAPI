package com.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue
    @Column(name = "task_id")
    private Integer taskId;

    @NotBlank(message = "'name' field cannot be empty")
    @Size(min = 3, max = 50, message = "'name' field must contain {min} - {max} characters")
    @Column(nullable = false, length = 50)
    private String name;

    private Integer numberInSequence;

    @Size(min = 3, max = 1000, message = "'description' field must contain {min} - {max} characters")
    @Column(length = 1000)
    private String description;

    @CreationTimestamp
    @Column(name = "added_date_time", nullable = false, updatable = false)
    private LocalDateTime addedDateTime;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnoreProperties({"tasks"})
    private Project project;

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumberInSequence() {
        return numberInSequence;
    }

    public void setNumberInSequence(Integer numberInSequence) {
        this.numberInSequence = numberInSequence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getAddedDateTime() {
        return addedDateTime;
    }

    public void setAddedDateTime(LocalDateTime addedDateTime) {
        this.addedDateTime = addedDateTime;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Task() {

    }

    public Task(Integer taskId, String name, Integer numberInSequence, String description) {
        this.taskId = taskId;
        this.name = name;
        this.numberInSequence = numberInSequence;
        this.description = description;
    }
}