/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.controller;

/**
 *
 * @author cachaww
 */

import com.example.tasque.dto.*;
import com.example.tasque.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

     @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody ProjectRequestDTO request, Authentication authentication) {
        ProjectResponseDTO response = projectService.createProject(request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects(Authentication authentication) {
        String username = authentication.getName();
        List<ProjectResponseDTO> projects = projectService.getProjectsByUser(username);
        return ResponseEntity.ok(projects);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<String> addMember(@PathVariable String projectId, @RequestParam String username) {
        boolean result = projectService.addMember(projectId, username);
        if (result) return ResponseEntity.ok("Anggota ditambahkan");
        return ResponseEntity.badRequest().body("Gagal menambahkan anggota");
    }

    @DeleteMapping("/{projectId}/members")
    public ResponseEntity<String> removeMember(@PathVariable String projectId, @RequestParam String username) {
        boolean result = projectService.removeMember(projectId, username);
        if (result) return ResponseEntity.ok("Anggota dihapus");
        return ResponseEntity.badRequest().body("Gagal menghapus anggota");
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<String>> getMembers(@PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getMemberUsernames(projectId));
    }
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByMember(@RequestParam String username) {
        List<ProjectResponseDTO> projects = projectService.getProjectsByMember(username);
        return ResponseEntity.ok(projects);
    }
    

}
