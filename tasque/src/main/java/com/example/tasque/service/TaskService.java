/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.service;

/**
 *
 * @author Athaya
 */
import com.example.tasque.dto.TagDTO;
import com.example.tasque.dto.TaskCommentDTO;
import com.example.tasque.dto.TaskRequestDTO;
import com.example.tasque.dto.TaskResponseDTO;
import com.example.tasque.dto.UserSummaryDTO;
import com.example.tasque.model.*;
import com.example.tasque.repository.*;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private TagRepository tagRepository;
    
    @Autowired
    private ProjectMembershipRepository membershipRepo;
    
    @Autowired
    private TagService tagService;
    
    @Autowired
    private NotificationService notificationService;
    
    public TaskResponseDTO createTask(TaskRequestDTO dto, User Creator) {
        String id = "task-" + UUID.randomUUID().toString().substring(0, 8);
        
        User assignedTo = userRepository.findByUsername(dto.getAssignedTo())
                .orElseThrow(() -> new NoSuchElementException("User dengan ID " + dto.getAssignedTo() + " tidak ditemukan"));
        
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new NoSuchElementException("Proyek tidak ditemukan"));
        
        boolean isMember = membershipRepo.existsByProjectAndUser(project, assignedTo);
        if (!isMember) {
            throw new IllegalStateException("User yang ditugaskan bukan anggota proyek.");
        }
        
        if (dto.getStart().isBefore(project.getStart()) || dto.getDeadline().isAfter(project.getDeadline())) {
            throw new IllegalStateException("Start dan deadline tugas harus berada dalam rentang waktu proyek.");
        }
        
        List<Tag> tags = dto.getTagNames().stream()
            .map(tagService::getOrCreateTagByName)
            .collect(Collectors.toList());
        
        requireManagerRole(project, Creator);
        
        Task task = Task.builder()
                .id(id)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .priority(dto.getPriority())
                .start(dto.getStart())
                .deadline(dto.getDeadline())
                .updatedAt(LocalDateTime.now())
                .assignedTo(assignedTo)
                .project(project)
                .tags(tags)
                .comments(Collections.emptyList())
                .build();

        Task saved = taskRepository.save(task);
        
        notificationService.sendNotification(
            assignedTo,
            "Task Assigned",
            "You have been assigned to task: " + task.getTitle(),
            "/task-detail.html?id=" + task.getId(),
            NotificationType.ASSIGNMENT
        );
        
        return mapToResponseDTO(saved);
    }
    
    public List<TaskResponseDTO> getTasksByProject(String projectId, User requester) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new NoSuchElementException("Proyek tidak ditemukan"));

        requireProjectMembership(project, requester);

        List<Task> tasks = taskRepository.findByProject(project);
        return tasks.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public TaskResponseDTO getTaskById(String taskId, User requester) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new NoSuchElementException("Task tidak ditemukan"));
        
        requireProjectMembership(task.getProject(), requester);

        return mapToResponseDTO(task);
    }
    
    public TaskResponseDTO updateTask(String taskId, TaskRequestDTO dto, User updater) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new NoSuchElementException("Task tidak ditemukan"));
        
        Project project = task.getProject();

        boolean isStatusChange = false;
        if(task.getStatus() != dto.getStatus()){
            isStatusChange = true;
        }
        
        if (dto.getStart().isBefore(project.getStart()) || dto.getDeadline().isAfter(project.getDeadline())) {
            throw new IllegalStateException("Start dan deadline tugas harus berada dalam rentang waktu proyek.");
        }
        
        if (dto.getAssignedTo() != null) {
            User assignedTo = userRepository.findByUsername(dto.getAssignedTo())
                .orElseThrow(() -> new NoSuchElementException("User dengan ID " + dto.getAssignedTo() + " tidak ditemukan"));
            
            boolean isMember = membershipRepo.existsByProjectAndUser(project, assignedTo);
            if (!isMember) {
                throw new IllegalStateException("User yang ditugaskan bukan anggota proyek.");
            }
            
            User assignedUser = userRepository.findByUsername(dto.getAssignedTo())
                .orElseThrow(() -> new NoSuchElementException("User assigned tidak ditemukan"));
            task.setAssignedTo(assignedUser);
        }

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());
        task.setStart(dto.getStart());
        task.setDeadline(dto.getDeadline());
        
        if (dto.getTagNames() != null) {
            // Simpan tag lama untuk pengecekan penghapusan
            List<Tag> oldTags = new ArrayList<>(task.getTags());

            // Proses tag baru: buat jika belum ada
            List<Tag> updatedTags = dto.getTagNames().stream()
                .map(name -> tagRepository.findByName(name)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setId(UUID.randomUUID().toString());
                        newTag.setName(name);
                        return tagRepository.save(newTag);
                    }))
                .collect(Collectors.toList());

            task.setTags(updatedTags);
            taskRepository.save(task); // Simpan dulu perubahan task

            // Cek dan hapus tag lama yang tidak lagi digunakan
            List<Tag> removedTags = oldTags.stream()
            .filter(tag -> !updatedTags.contains(tag))
            .collect(Collectors.toList());

            tagService.removeUnusedTags(removedTags);
        }

        Task updated = taskRepository.save(task);
        
        if (task.getStatus() == TaskStatus.REVIEW && isStatusChange) {
            List<ProjectMembership> managers = membershipRepo.findByProjectAndRole(task.getProject(), ProjectRole.PROJECT_MANAGER);
            for (ProjectMembership m : managers) {
                User manager = m.getUser();
                notificationService.sendNotification(
                    manager,
                    "Task Masuk Tahap Review",
                    "Task \"" + task.getTitle() + "\" sudah siap direview.",
                    "/task-detail.html?id=" + task.getId(),
                    NotificationType.STATUS_UPDATE
                );
            }
        } else if (isStatusChange) {
            if (task.getAssignedTo() != null) {
                notificationService.sendNotification(
                    task.getAssignedTo(),
                    "Task Status Updated",
                    "Status task \"" + task.getTitle() + "\" diubah ke " + dto.getStatus(),
                    "/task-detail.html?id=" + task.getId(),
                NotificationType.STATUS_UPDATE
                );
            }
        }
        
        return mapToResponseDTO(updated);
    }

    public void deleteTask(String taskId, User requester) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new NoSuchElementException("Task tidak ditemukan"));

        requireManagerRole(task.getProject(), requester);

        taskRepository.delete(task);
    }
    
    private void requireProjectMembership(Project project, User user) {
        membershipRepo.findByProjectAndUser(project, user)
            .orElseThrow(() -> new SecurityException("Anda bukan anggota proyek ini"));
}
    
    private void requireManagerRole(Project project, User requester) {
        ProjectMembership member = membershipRepo.findByProjectAndUser(project, requester)
                .orElseThrow(() -> new SecurityException("Anda bukan anggota proyek ini"));

        if (member.getRole() != ProjectRole.PROJECT_MANAGER) {
            throw new SecurityException("Akses ditolak: hanya Project Manager yang dapat melakukan aksi ini");
        }
    }
    
    private TaskResponseDTO mapToResponseDTO(Task task) {
        Map<String, ProjectRole> userRoleMap = new HashMap<>();
        for (TaskComment comment : task.getComments()) {
            String userId = comment.getAuthor().getId();
            userRoleMap.computeIfAbsent(userId, uid ->
                membershipRepo.findByProjectAndUser(task.getProject(), comment.getAuthor())
                    .map(ProjectMembership::getRole)
                    .orElse(null)
            );
        }

    return TaskResponseDTO.builder()
        .id(task.getId())
        .title(task.getTitle())
        .description(task.getDescription())
        .status(task.getStatus() != null ? task.getStatus().name() : null)
        .priority(task.getPriority() != null ? task.getPriority().name() : null)
        .start(task.getStart())
        .updatedAt(task.getUpdatedAt())
        .deadline(task.getDeadline())
        .assignedTo(task.getAssignedTo() != null
            ? UserSummaryDTO.builder()
                .id(task.getAssignedTo().getId())
                .name(task.getAssignedTo().getUsername())
                .email(task.getAssignedTo().getEmail())
                .build()
                : null)
        .project(task.getProject())
        .tags(task.getTags() != null
            ? task.getTags().stream()
                .map(tag -> TagDTO.builder().id(tag.getId()).name(tag.getName()).build())
                .collect(Collectors.toList())
            : Collections.emptyList())
        .comments(task.getComments() != null
            ? task.getComments().stream()
                .map(comment -> TaskCommentDTO.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .author(UserSummaryDTO.builder()
                        .id(comment.getAuthor().getId())
                        .name(comment.getAuthor().getUsername())
                        .email(comment.getAuthor().getEmail())
                        .role(userRoleMap.get(comment.getAuthor().getId()))
                        .build())
                    .build())
                .collect(Collectors.toList())
            : Collections.emptyList())
        .build();
    }
}
