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
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> task = new ArrayList<>();

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



    public List<Task> getTask() { return task; }
    public void setTask(List<Task> task) { this.task = task; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public List<User> getMembers() { return members; }
    public void setMembers(List<User> members) { this.members = members; }


    public double getProgress() {
        if (task == null || task.isEmpty()) return 0.0;

        long completed = task.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .count();

        return (completed * 100.0) / task.size();
    }

    public boolean addMember(User user) {
        return members.add(user);
    }

    public boolean removeMember(String userId) {
        return members.removeIf(user -> user.getId().equals(userId));
    }

    public boolean addTask(Task task) {
        task.setProject(this);
        return this.task.add(task);
    }

    public boolean removeTask(String taskId) {
        return task.removeIf(t -> t.getTitle().equals(taskId));
    }

    public void displayProgress() {
        System.out.println("Project Progress: " + getProgress() + "%");
    }
}
