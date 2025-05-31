/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.repository;

import com.example.tasque.model.Task;
import com.example.tasque.model.Project;
import com.example.tasque.model.Tag;
import com.example.tasque.model.TaskStatus;
import com.example.tasque.model.User;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByProject(Project project);
    List<Task> findByAssignedTo(User assignee);
    List<Task> findByProjectId(String projectId);
    List<Task> findByTagsContaining(Tag tag);
    List<Task> findByTagsAndProject(Tag tag, Project project);
    List<Task> findByDeadlineBetweenAndStatusNot(LocalDateTime now, LocalDateTime nextDay, TaskStatus taskStatus);
    boolean existsByProjectAndAssignedTo(Project project, User userToRemove);
}
