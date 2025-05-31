/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.model;

/**
 *
 * @author cachaww
 */
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Project {
    @Id
    private String id;

    private String name;

    private String description;

    private LocalDateTime start;

    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    public Project() {}

    public Project(String id, String name, String description, LocalDateTime start, LocalDateTime deadline, User createdBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.start = start;
        this.deadline = deadline;
        this.createdBy = createdBy;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

}
