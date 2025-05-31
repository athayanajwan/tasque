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
import com.example.tasque.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    List<Project> findByCreatedBy(User user);
    
    List<Project> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT COUNT(p) FROM Project p")
    long countProject();
}
