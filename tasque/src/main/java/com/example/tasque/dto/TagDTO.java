/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.dto;

/**
 *
 * @author Athaya
 */
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {
    private String id;
    private String name;
}
