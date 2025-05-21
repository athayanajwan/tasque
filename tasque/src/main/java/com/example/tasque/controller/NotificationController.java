/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author regianyogaswara
 */
package com.example.tasque.controller;

import com.example.tasque.dto.NotificationDTO;
import com.example.tasque.model.Notification;
import com.example.tasque.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService service;

    @PostMapping
    public Notification createNotification(@RequestBody NotificationDTO dto) {
        return service.createNotification(dto);
    }

    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(@PathVariable String userId) {
        return service.getNotificationsByUser(userId);
    }
}

