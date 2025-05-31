/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.service;

/**
 *
 * @author Athaya
 */
import com.example.tasque.dto.TaskCommentDTO;
import com.example.tasque.dto.UserSummaryDTO;
import com.example.tasque.model.*;
import com.example.tasque.repository.ProjectMembershipRepository;
import com.example.tasque.repository.TaskCommentRepository;
import com.example.tasque.repository.TaskRepository;
import com.example.tasque.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor
public class TaskCommentService {

    @Autowired
    private TaskCommentRepository commentRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectMembershipRepository membershipRepository;

    @Autowired
    private NotificationService notificationService;
    
    public TaskCommentDTO addComment(String taskId, String userId, String content) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task tidak ditemukan"));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        ProjectMembership membership = membershipRepository.findByProjectAndUser(task.getProject(), author)
                .orElseThrow(() -> new SecurityException("User bukan bagian dari project ini"));

        TaskComment comment = TaskComment.builder()
                .id("comment-" + UUID.randomUUID().toString().substring(0, 8))
                .task(task)
                .author(author)
                .content(content)
                .build();

        TaskComment saved = commentRepository.save(comment);

        if (task.getAssignedTo() != null && !task.getAssignedTo().getId().equals(author.getId())) {
            notificationService.sendNotification(
                task.getAssignedTo(),
                "Komentar Baru di Task",
                author.getUsername() + " mengomentari task: " + task.getTitle(),
                "/task-detail.html?id=" + task.getId(),
                NotificationType.COMMENT
            );
        }
        
        return TaskCommentDTO.builder()
                .id(saved.getId())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .author(UserSummaryDTO.builder()
                        .id(author.getId())
                        .name(author.getUsername())
                        .email(author.getEmail())
                        .role(membership.getRole())
                        .build())
                .build();
    }

    public void deleteComment(String commentId, User requester) {
        TaskComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Komentar tidak ditemukan"));

        Task task = comment.getTask();
        Project project = task.getProject();

        boolean isAuthor = comment.getAuthor().getId().equals(requester.getId());
        boolean isManager = membershipRepository.findByProjectAndUser(project, requester)
        .map(membership -> membership.getRole() == ProjectRole.PROJECT_MANAGER)
        .orElse(false);

        if (!isAuthor && !isManager) {
            throw new SecurityException("Hanya pemilik komentar atau Project Manager yang dapat menghapus komentar ini");
        }

        commentRepository.delete(comment);
    }

    public List<TaskCommentDTO> getCommentsByTask(String taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task tidak ditemukan"));

        List<TaskComment> comments = commentRepository.findByTask(task);

        return comments.stream()
            .map(comment -> {
                User author = comment.getAuthor();
                Project project = task.getProject();

                ProjectRole role = membershipRepository.findByProjectAndUser(project, author)
                        .map(ProjectMembership::getRole)
                        .orElse(null);

                return TaskCommentDTO.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .author(UserSummaryDTO.builder()
                                .id(author.getId())
                                .name(author.getUsername())
                                .email(author.getEmail())
                                .role(role)
                                .build())
                        .build();
            })
            .collect(Collectors.toList());
    }
}