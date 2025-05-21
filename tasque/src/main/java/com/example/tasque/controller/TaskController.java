package com.example.tasque.controller;

import com.example.tasque.dto.TaskRequestDTO;
import com.example.tasque.model.Task;
import com.example.tasque.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public Task createTask(@RequestBody TaskRequestDTO request) {
        return taskService.createTask(request);
    }

    @PutMapping("/{id}/status")
    public Task updateTaskStatus(@PathVariable String id, @RequestParam String status) {
        return taskService.updateTaskStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }
    
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable String id, @RequestBody TaskRequestDTO request) {
        return taskService.updateTask(id, request);
    }



}
