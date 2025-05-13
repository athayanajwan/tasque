/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.dto;

/**
 *
 * @author cachaww
 */
import java.util.Date;

public class ProjectRequestDTO {
    private String name;
    private String description;
    private Date deadline;

    public ProjectRequestDTO() {}

    public ProjectRequestDTO(String name, String description, Date deadline) {
        this.name = name;
        this.description = description;
        this.deadline = deadline;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
}
