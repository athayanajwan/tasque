/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.service;

/**
 *
 * @author cachaww
 */
import com.example.tasque.dto.ProjectRequestDTO;
import com.example.tasque.dto.ProjectResponseDTO;
import com.example.tasque.dto.ProjectResponseDTO.MemberInfo;
import com.example.tasque.model.*;
import com.example.tasque.repository.ProjectMembershipRepository;
import com.example.tasque.repository.ProjectRepository;
import com.example.tasque.repository.UserRepository;
import com.example.tasque.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private ProjectMembershipRepository membershipRepo;

    @Autowired
    private UserRepository userRepo;
    
    public ProjectResponseDTO createProject(ProjectRequestDTO request, User creator){
        long count = projectRepo.countProject()+1;
        String id = String.format("project-%05d", count);
        Project project = new Project(id, request.getName(), request.getDescription(), LocalDate.now(), request.getDeadline(), creator);
        
        Project saved = projectRepo.save(project);
        
        String projectMemberId = creator.getId().substring(creator.getId().length() - 5)+"-"+id.substring(id.length() - 5);
        
        ProjectMembership membership = new ProjectMembership(projectMemberId, saved, creator, ProjectRole.PROJECT_MANAGER);
         membershipRepo.save(membership);

        return mapToResponseDTO(saved);
    }
    
    public ProjectResponseDTO updateProject(String projectId, ProjectRequestDTO request, User requester) {
        Project project = getProjectEntityById(projectId, requester);
        requireManagerRole(project, requester);

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setDeadline(request.getDeadline());

        Project updated = projectRepo.save(project);
        return mapToResponseDTO(updated);
    }
    
    @Transactional
    public void deleteProject(String projectId, User requester) {
        Project project = getProjectEntityById(projectId, requester);
        requireManagerRole(project, requester);
        
        List<ProjectMembership> memberships = membershipRepo.findByProject(project);
        membershipRepo.deleteAll(memberships);
        
        projectRepo.delete(project);
    }
    
    public List<ProjectResponseDTO> getProjectsByUser(User user) {
        return membershipRepo.findByUser(user).stream()
                .map(ProjectMembership::getProject)
                .map(this::mapToResponseDTO)
                .toList();
    }
    
    public ProjectResponseDTO getProjectById(String projectId, User requester) {
        Project project = getProjectEntityById(projectId, requester);
        List<MemberInfo> memberInfos = membershipRepo.findByProject(project).stream()
        .map(m -> {
            MemberInfo info = new MemberInfo();
            info.setUserId(m.getUser().getId());
            info.setUsername(m.getUser().getUsername());
            info.setRole(m.getRole());
            return info;
        }).collect(Collectors.toList());
            
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCreatedBy(project.getCreatedBy().getUsername());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setDeadline(project.getDeadline());
        dto.setMembers(memberInfos);

        return dto;
    }
    
    public void addMemberToProject(String projectId, String username, String roleStr, User requester) {
        Project project = getProjectEntityById(projectId, requester);
        requireManagerRole(project, requester);

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User tidak ditemukan"));

        ProjectRole role = ProjectRole.valueOf(roleStr.toUpperCase());

        if (membershipRepo.findByProjectAndUser(project, user).isPresent()) {
            throw new IllegalStateException("User sudah menjadi anggota proyek ini");
        }
        
        String projectMemberId = user.getId().substring(user.getId().length() - 5)+"-"+projectId.substring(projectId.length() - 5);
        ProjectMembership member = new ProjectMembership();
        member.setId(projectMemberId);
        member.setProject(project);
        member.setUser(user);
        member.setRole(role);
        membershipRepo.save(member);
    }
    
    public void removeMemberFromProject(String projectId, String username, User requester) {
    Project project = getProjectEntityById(projectId, requester);
    requireManagerRole(project, requester);

    User userToRemove = userRepo.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User tidak ditemukan"));

    ProjectMembership membership = membershipRepo.findByProjectAndUser(project, userToRemove)
            .orElseThrow(() -> new EntityNotFoundException("User bukan anggota proyek ini"));

    if (userToRemove.getId().equals(requester.getId())) {
        throw new IllegalStateException("Project Manager tidak dapat menghapus dirinya sendiri.");
    }

    membershipRepo.delete(membership);
    }
    

    
    private Project getProjectEntityById(String projectId, User requester) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project tidak ditemukan"));

        membershipRepo.findByProjectAndUser(project, requester)
                .orElseThrow(() -> new SecurityException("Anda bukan anggota proyek ini"));

        return project;
    }

    private void requireManagerRole(Project project, User requester) {
        ProjectMembership member = membershipRepo.findByProjectAndUser(project, requester)
                .orElseThrow(() -> new SecurityException("Anda bukan anggota proyek ini"));

        if (member.getRole() != ProjectRole.PROJECT_MANAGER) {
            throw new SecurityException("Akses ditolak: hanya Project Manager yang dapat melakukan aksi ini");
        }
    }
    
    private ProjectResponseDTO mapToResponseDTO(Project project) {
        return new ProjectResponseDTO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedBy().getUsername(),
                project.getCreatedAt(),
                project.getDeadline()
        );
    }

    public double getProjectProgress(String projectId) {
        Optional<Project> projectOpt = projectRepo.findById(projectId);

        if (projectOpt.isEmpty()) {
            throw new IllegalArgumentException("Project with ID " + projectId + " not found.");
        }

        Project project = projectOpt.get();
        return project.getProgress();
    }
    
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepo.findAll().stream().map(this::mapToResponseDTO).toList();
    }
    
}
