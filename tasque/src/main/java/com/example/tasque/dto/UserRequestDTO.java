/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.dto;

/**
 *
 * @author Athaya
 */
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRequestDTO {
    
    private String username;

    private String password;

    private String email;
    
    private String phoneNumber;
    
    private String deskripsi;

    public UserRequestDTO() {}

    public UserRequestDTO(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public UserRequestDTO(String username, String email, String password, String phoneNumber, String deskripsi) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.deskripsi = deskripsi;
    }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber(){
        return phoneNumber;
    }
    
    public void setPhoneNumber(String number){
        this.phoneNumber = number;
    }
    
    public String getDeskripsi(){
        return deskripsi;
    }
    
    public void setDeskripsi(String deskripsi){
        this.deskripsi = deskripsi;
    }
}
