/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.controller;

import com.example.tasque.dto.TaskRequestDTO;
import com.example.tasque.dto.TaskResponseDTO;
import com.example.tasque.model.*;
import com.example.tasque.service.*;
import com.example.tasque.util.CurrentUserUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final CurrentUserUtil currentUserUtil;

    private User getCurrentUser() {
        return currentUserUtil.getCurrentUser();
    }
    
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskRequestDTO request) {
        User currentUser = getCurrentUser();
        TaskResponseDTO createdTask = taskService.createTask(request, currentUser);
        return new ResponseEntity<>(createdTask, HttpStatus.OK);
    }

    // GET ALL TASKS
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(@RequestParam String projectId) {
        User currentUser = getCurrentUser();
        List<TaskResponseDTO> tasks = taskService.getTasksByProject(projectId, currentUser);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // GET TASK BY ID
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable String taskId) {
        User currentUser = getCurrentUser();
        TaskResponseDTO task = taskService.getTaskById(taskId, currentUser);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    // UPDATE TASK
        @PutMapping("/{taskId}")
        public ResponseEntity<TaskResponseDTO> updateTask(
                @PathVariable String taskId,
                @RequestBody TaskRequestDTO request) {
            User currentUser = getCurrentUser();
            TaskResponseDTO updatedTask = taskService.updateTask(taskId, request, currentUser);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        }

    // DELETE TASK
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        User currentUser = getCurrentUser();
        taskService.deleteTask(taskId, currentUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
