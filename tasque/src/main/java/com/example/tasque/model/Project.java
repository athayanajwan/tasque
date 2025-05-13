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
import java.util.*;

@Entity
public class Project {
    @Id
    private String id;

    private String name;

    private String description;

    private Date createdAt;

    private Date deadline;

    @ManyToOne
    private User createdBy;

    @ManyToMany
    private List<User> members = new ArrayList<>();

    public Project() {}

    public Project(String id, String name, String description, Date createdAt, Date deadline, User createdBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.createdBy = createdBy;
        this.members = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public List<User> getMembers() { return members; }
    public void setMembers(List<User> members) { this.members = members; }
}
