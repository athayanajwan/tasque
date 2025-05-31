/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.dto;

/**
 *
 * @author Athaya
 */
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.example.tasque.model.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {
    private String id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime start;
    private LocalDateTime updatedAt;
    private LocalDateTime deadline;
    private UserSummaryDTO assignedTo;
    private Project project;
    private List<TagDTO> tags;
    private List<TaskCommentDTO> comments;
}
