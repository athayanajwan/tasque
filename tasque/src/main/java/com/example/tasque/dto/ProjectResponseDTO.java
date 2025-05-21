/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.dto;

/**
 *
 * @author cachaww
 */
import com.example.tasque.model.ProjectRole;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Date;

public class ProjectResponseDTO {
    private String id;
    private String name;
    private String description;
    private String createdBy;
    private LocalDate createdAt;
    private LocalDateTime deadline;
    private List<MemberInfo> members;
    
    
    
    public static class MemberInfo {
        private String userId;
        private String username;
        private ProjectRole role;
        
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public ProjectRole getRole() {
            return role;
        }

        public void setRole(ProjectRole role) {
            this.role = role;
        }
    }
    
    public ProjectResponseDTO() {}

    public ProjectResponseDTO(String id, String name, String description, String createdBy, LocalDate createdAt, LocalDateTime deadline, List<MemberInfo> user) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.members = user;
    }
    
    public ProjectResponseDTO(String id, String name, String description, String createdBy, LocalDate createdAt, LocalDateTime deadline) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.deadline = deadline;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    
    public List<MemberInfo> getMembers() {
        return members;
    }

    public void setMembers(List<MemberInfo> members) {
        this.members = members;
    }
}

