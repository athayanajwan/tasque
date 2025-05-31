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
public class TaskRequestDTO {
    private String title;
    private String description;
    private String projectId;
    private String assignedTo;
    private TaskPriority priority; 
    private TaskStatus status;
    private LocalDateTime start;
    private LocalDateTime deadline;
    private List<String> tagNames;
}
