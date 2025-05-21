/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.model;
import jakarta.persistence.*;
/**
 *
 * @author Athaya
 */
@Entity
public class ProjectMembership {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ProjectRole role;

    public ProjectMembership(){}
    
    public ProjectMembership(String id, Project project, User user, ProjectRole role){
        this.id = id;
        this.project = project;
        this.user = user;
        this.role = role;
    }
    
    public String getId(){ return id; }
    public void setId(String id){ this.id = id; }
    
    public Project getProject(){ return project; }
    public void setProject(Project project){ this.project = project; }
    
    public User getUser(){ return user; }
    public void setUser(User user){ this.user = user; }
    
    public ProjectRole getRole(){ return role; }
    public void setRole(ProjectRole role){ this.role = role; }
}