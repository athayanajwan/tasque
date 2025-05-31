/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author regianyogaswara
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    private String id;

    private String title;
    private String message;
    
    @Column(name = "is_read")
    @Builder.Default
    private boolean isRead = false;

    private LocalDateTime createdAt;

    @ManyToOne
    private User recipient;

    private String link;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    
}
