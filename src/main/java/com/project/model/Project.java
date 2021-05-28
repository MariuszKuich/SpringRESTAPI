package com.project.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue
    @Column(name = "project_id")
    private Integer projectId;

    @NotBlank(message = "'name' field cannot be empty")
    @Size(min = 3, max = 50, message = "'name' field must contain {min} - {max} characters")
    @Column(nullable = false, length = 50)
    private String name;

    @Size(min = 10, max = 1000, message = "'description' field must contain {min} - {max} characters")
    @Column(length = 1000)
    private String description;

    @CreationTimestamp
    @Column(name = "creation_date_time", nullable = false, updatable = false)
    private LocalDateTime creationDateTime;

    @Column(name = "handin_date")
    private LocalDate handinDate;

    @OneToMany(mappedBy = "project")
    @JsonIgnoreProperties({"project"})
    private List<Task> tasks;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public LocalDate getHandinDate() {
        return handinDate;
    }

    public void setHandinDate(LocalDate handinDate) {
        this.handinDate = handinDate;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Project() {

    }

    public Project(Integer projectId, String name, String description,
                   LocalDateTime creationDateTime, LocalDate handinDate) {
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.creationDateTime = creationDateTime;
        this.handinDate = handinDate;
    }

    @Override
    public String toString() {
        return String.format("ID: %s, Name: %s, Description: %s, CreationDateTime: %s, HandinDate: %s",
                projectId, name, description, creationDateTime, handinDate);
    }
}