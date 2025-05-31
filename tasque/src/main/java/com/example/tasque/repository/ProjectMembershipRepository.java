/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.tasque.repository;

/**
 *
 * @author cachaww
 */
import com.example.tasque.model.Project;
import com.example.tasque.model.ProjectMembership;
import com.example.tasque.model.ProjectRole;
import com.example.tasque.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, String> {
    List<ProjectMembership> findByUser(User user);
    Optional<ProjectMembership> findByProjectAndUser(Project project, User user);
    List<ProjectMembership> findByProject(Project project);
    List<ProjectMembership> findByProjectAndRole(Project project, ProjectRole projectRole);
    boolean existsByProjectAndUser(Project project, User assignedTo);
}
