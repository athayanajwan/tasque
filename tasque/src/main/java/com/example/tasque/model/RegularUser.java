/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tasque.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users")
public class RegularUser {

    @Id
    private String id;

    private String name;

    // Getters & Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegularUser)) return false;
        RegularUser user = (RegularUser) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

