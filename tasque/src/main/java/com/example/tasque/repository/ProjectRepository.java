/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.repository;

/**
 *
 * @author cachaww
 */
import com.example.tasque.model.Project;
import com.example.tasque.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {
    List<Project> findByMembersContaining(User user);
    List<Project> findByCreatedBy(User user);
}