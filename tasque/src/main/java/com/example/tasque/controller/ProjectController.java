/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.controller;

/**
 *
 * @author Athaya
 */
import com.example.tasque.dto.ProjectRequestDTO;
import com.example.tasque.dto.ProjectResponseDTO;
import com.example.tasque.model.User;
import com.example.tasque.service.ProjectService;
import com.example.tasque.util.CurrentUserUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    private User getCurrentUser() {
        return CurrentUserUtil.getCurrentUser();
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody ProjectRequestDTO request) {
        User user = getCurrentUser();
        ProjectResponseDTO response = projectService.createProject(request, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable String projectId, @RequestBody ProjectRequestDTO request) {
        User user = getCurrentUser();
        ProjectResponseDTO response = projectService.updateProject(projectId, request, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getUserProjects() {
        User user = getCurrentUser();
        List<ProjectResponseDTO> projects = projectService.getProjectsByUser(user);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable String projectId) {
        User user = getCurrentUser();
        ProjectResponseDTO project = projectService.getProjectById(projectId, user);
        return ResponseEntity.ok(project);
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<String> addMember(
            @PathVariable String projectId,
            @RequestParam String username,
            @RequestParam String role) {
        User user = getCurrentUser();
        projectService.addMemberToProject(projectId, username, role, user);
        return ResponseEntity.ok("Anggota berhasil ditambahkan");
    }

    @DeleteMapping("/{projectId}/members")
    public ResponseEntity<String> removeMember(
            @PathVariable String projectId,
            @RequestParam String username) {
        User user = getCurrentUser();
        projectService.removeMemberFromProject(projectId, username, user);
        return ResponseEntity.ok("Anggota berhasil dihapus");
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable String projectId) {
        User user = getCurrentUser();
        projectService.deleteProject(projectId, user);
        return ResponseEntity.ok("Proyek berhasil dihapus");
    }
}
