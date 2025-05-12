/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.service;
import com.example.tasque.dto.UserRequestDTO;
import com.example.tasque.dto.UserResponseDTO;
import com.example.tasque.model.User;
import com.example.tasque.repository.UserRepository;
import com.example.tasque.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
/**
 *
 * @author Athaya
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public UserResponseDTO register(UserRequestDTO request){
        if (userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new IllegalArgumentException("Username sudah digunakan");
        }
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email sudah digunakan");
        }
        
        long count = userRepository.countUsers()+1;
        String id = String.format("user-%05d", count);
        
        User user = new User(id, request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        
        try {
            userRepository.save(user);
            System.out.println("User berhasil disimpan ke DB.");
        } catch (Exception e) {
            System.err.println("Gagal menyimpan user: " + e.getMessage());
            e.printStackTrace();
        }

        
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail());
    }
    
    public String login(UserRequestDTO request){


        User user = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername()))
                .orElseThrow(() -> new UsernameNotFoundException("Username/Password salah"));
        
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Username/Password salah");
        }
        
        return jwtUtil.generateToken(user.getUsername());
    }
    
    public UserResponseDTO updateProfile(String id, UserRequestDTO request){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(""));
        
        if(!user.getUsername().equals(request.getUsername())){
            userRepository.findByUsername(request.getUsername()).ifPresent(existing -> {throw new IllegalArgumentException("Username sudah digunakan");});
            user.setUsername(request.getUsername());
        }
        
        if(!user.getEmail().equals(request.getEmail())){
            userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {throw new IllegalArgumentException("Email sudah digunakan");});
            user.setEmail(request.getEmail());
        }
        
        if(request.getPassword() != null && !request.getPassword().isBlank()){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        userRepository.save(user);
        
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail());
    }
    
    public UserResponseDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail());
    }
    
    public UserResponseDTO updateCurrentUser(String username, UserRequestDTO request) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return updateProfile(user.getId(), request); // Gunakan metode yang sudah ada
    }
}
