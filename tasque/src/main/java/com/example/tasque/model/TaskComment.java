/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

/**
 *
 * @author Athaya
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskComment {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private String content;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}