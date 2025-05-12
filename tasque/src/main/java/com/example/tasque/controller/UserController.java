/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.controller;
import com.example.tasque.dto.UserRequestDTO;
import com.example.tasque.dto.UserResponseDTO;
import com.example.tasque.model.User;
import com.example.tasque.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
/**
 *
 * @author Athaya
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO request){
        UserResponseDTO response = userService.register(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody UserRequestDTO request){
        String jwt = userService.login(request);
        return ResponseEntity.ok(jwt);
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateCurrentUser(Authentication authentication,@Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO response = userService.updateCurrentUser(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        UserResponseDTO response = userService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(response);
    }
}
