package com.example.tasque.service;

import com.example.tasque.dto.TaskRequestDTO;
import com.example.tasque.model.Task;
import com.example.tasque.model.TaskPriority;
import com.example.tasque.model.TaskStatus;
import com.example.tasque.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(TaskRequestDTO request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(TaskPriority.valueOf(request.getPriority()));
        task.setStatus(TaskStatus.TO_DO);
        task.setCreatedAt(new Date());
        task.setDeadline(request.getDeadline());
        return taskRepository.save(task);
    }

    public Task updateTaskStatus(String id, String status) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setStatus(TaskStatus.valueOf(status));
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found with ID: " + id);
        }
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTask(String id, TaskRequestDTO request) {
    Optional<Task> taskOptional = taskRepository.findById(id);
    if (taskOptional.isPresent()) {
        Task task = taskOptional.get();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(TaskPriority.valueOf(request.getPriority()));
        task.setDeadline(request.getDeadline());
        return taskRepository.save(task);
    } else {
        throw new RuntimeException("Task not found with ID: " + id);
    }
}

    
    

}
