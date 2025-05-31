/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.repository;

/**
 *
 * @author Athaya
 */
import com.example.tasque.model.TaskComment;
import com.example.tasque.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskCommentRepository extends JpaRepository<TaskComment, String> {
    List<TaskComment> findByTask(Task task);
}
