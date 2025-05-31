/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.controller;

import com.example.tasque.model.*;
import com.example.tasque.service.NotificationService;
import com.example.tasque.util.CurrentUserUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 *
 * @author regianyogaswara
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserUtil currentUserUtil;

    @GetMapping
    public List<Notification> getMyNotifications() {
        User currentUser = currentUserUtil.getCurrentUser();
        return notificationService.getUserNotifications(currentUser.getId());
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
    }

}
