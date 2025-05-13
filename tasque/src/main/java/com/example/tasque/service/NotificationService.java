/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author regianyogaswara
 */
package com.example.tasque.service;

import com.example.tasque.dto.NotificationDTO;
import com.example.tasque.model.Notification;
import com.example.tasque.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(NotificationDTO dto) {
        Notification notif = new Notification();
        notif.setRecipientId(dto.getRecipientId());
        notif.setMessage(dto.getMessage());
        notif.setType(dto.getType());
        notif.setCreatedAt(LocalDateTime.now());
        notif.setRead(false);

        return notificationRepository.save(notif);
    }
    public List<Notification> getNotificationsByUser(String userId) {
        return notificationRepository.findByRecipientId(userId);
    }
}


