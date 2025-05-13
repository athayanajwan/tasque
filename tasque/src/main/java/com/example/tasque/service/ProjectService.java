/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.service;

/**
 *
 * @author cachaww
 */

import com.example.tasque.dto.*;
import com.example.tasque.model.*;
import com.example.tasque.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    
    public ProjectResponseDTO createProject(ProjectRequestDTO request, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Project project = new Project(UUID.randomUUID().toString(), request.getName(), request.getDescription(), new Date(), request.getDeadline(), creator);
        projectRepository.save(project);
        return toDTO(project);
    }

   
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll().stream().map(this::toDTO).toList();
    }

    
    public void deleteProject(String id) {
        projectRepository.deleteById(id);
    }

    
    public boolean addMember(String projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (project.getMembers().contains(user)) return false;
        project.getMembers().add(user);
        projectRepository.save(project);
        return true;
    }
    
    
    public boolean removeMember(String projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean removed = project.getMembers().remove(user);
        if (removed) projectRepository.save(project);
        return removed;
    }

    
    public List<String> getMemberUsernames(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return project.getMembers().stream().map(User::getUsername).toList();
    }

    
    public List<ProjectResponseDTO> getProjectsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Project> projects = projectRepository.findByCreatedBy(user);
        return projects.stream().map(this::toDTO).collect(Collectors.toList());
    }

    
    public List<ProjectResponseDTO> getProjectsByMember(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        List<Project> projects = projectRepository.findByMembersContaining(user);  
        return projects.stream().map(this::toDTO).collect(Collectors.toList());
    }


    private ProjectResponseDTO toDTO(Project project) {
        return new ProjectResponseDTO(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getCreatedBy().getUsername(),
            project.getCreatedAt(),
            project.getDeadline()
        );
    }
}
