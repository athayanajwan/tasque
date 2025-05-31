/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.service;

import com.example.tasque.model.*;
import com.example.tasque.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author regianyogaswara
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotification(User recipient, String title, String message, String link, NotificationType type) {
        String id = "notif-" + UUID.randomUUID().toString().substring(0, 8);
        Notification notification = new Notification();
        notification.setId(id);
        notification.setRecipient(recipient);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setLink(link);
        notification.setType(type);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(String id) {
        Notification notif = notificationRepository.findById(id).orElseThrow();
        notif.setRead(true);
        notificationRepository.save(notif);
    }
}
