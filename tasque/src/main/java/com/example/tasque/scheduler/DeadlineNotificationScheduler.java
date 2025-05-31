/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.scheduler;

import com.example.tasque.model.*;
import com.example.tasque.repository.*;
import com.example.tasque.service.NotificationService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author Athaya
 */
@Service
public class DeadlineNotificationScheduler {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectMembershipRepository membershipRepository;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *") 
    public void checkUpcomingDeadlines() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextDay = now.plusHours(24);

        List<Task> upcomingTasks = taskRepository.findByDeadlineBetweenAndStatusNot(
            now, nextDay, TaskStatus.COMPLETED
        );

        for (Task task : upcomingTasks) {
            if (task.getAssignedTo() != null) {
                notificationService.sendNotification(
                    task.getAssignedTo(),
                    "Deadline Mendekat",
                    "Task \"" + task.getTitle() + "\" memiliki deadline < 24 jam.",
                    "/task-detail.html?id=" + task.getId(),
                    NotificationType.DEADLINE
                );
            }
        }
    }
}
