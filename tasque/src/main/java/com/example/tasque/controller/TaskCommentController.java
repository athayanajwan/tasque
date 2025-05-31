/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.controller;

import com.example.tasque.dto.TaskCommentDTO;
import com.example.tasque.dto.TaskRequestDTO;
import com.example.tasque.dto.TaskResponseDTO;
import com.example.tasque.model.*;
import com.example.tasque.service.*;
import com.example.tasque.util.CurrentUserUtil;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Athaya
 */
@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    private final CurrentUserUtil currentUserUtil;

    private User getCurrentUser() {
        return currentUserUtil.getCurrentUser();
    }
    
    // Endpoint untuk menambahkan komentar
    @PostMapping
    public ResponseEntity<TaskCommentDTO> addComment(
            @PathVariable String taskId,
            @RequestParam String userId,
            @RequestBody Map<String, String> requestBody) {

        String content = requestBody.get("content");

        TaskCommentDTO newComment = taskCommentService.addComment(taskId, userId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
    }

    // Endpoint untuk menghapus komentar
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String taskId,
            @PathVariable String commentId,
            @RequestParam String userId) {

        User requester = getCurrentUser();

        taskCommentService.deleteComment(commentId, requester);
        return ResponseEntity.noContent().build();
    }
}
