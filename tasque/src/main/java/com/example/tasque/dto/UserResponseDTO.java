/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.dto;

/**
 *
 * @author Athaya
 */
public class UserResponseDTO {

    private String id;
    private String username;
    private String email;

    public UserResponseDTO() {}

    public UserResponseDTO(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public String getUserId() { return id; }
    public void setUserId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
