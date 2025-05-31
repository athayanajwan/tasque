/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.service;

/**
 *
 * @author Athaya
 */
import com.example.tasque.model.Tag;
import com.example.tasque.repository.TagRepository;
import com.example.tasque.repository.TaskRepository;
import com.example.tasque.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor
public class TagService {

    @Autowired
    private TagRepository tagRepository;
    
    @Autowired
    private TaskRepository taskRepository;

    public Tag getOrCreateTagByName(String name) {
    return tagRepository.findByName(name)
            .orElseGet(() -> {
                String id = "tag-" + UUID.randomUUID().toString().substring(0, 8);
                Tag newTag = Tag.builder().id(id).name(name).build();
                return tagRepository.save(newTag);
            });
}

    public void removeUnusedTags(List<Tag> tags) {
        for (Tag tag : tags) {
            List<Task> tasksUsingTag = taskRepository.findByTagsContaining(tag);
            if (tasksUsingTag.isEmpty()) {
                tagRepository.delete(tag);
            }
        }
    }
}