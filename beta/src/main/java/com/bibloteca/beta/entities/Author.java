package com.bibloteca.beta.entities;

import com.bibloteca.beta.enums.Role;
import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.OneToMany;
import java.util.List;
import javax.persistence.OneToOne;

@Entity
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Author implements Serializable {//implements serializable??
    
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String picture;
    
    @EqualsAndHashCode.Include
    private Integer dni;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    private Boolean alive;
    
    private ArrayList<Book> listBook;

    public Author() {
    }

    public Author(String id, String name, String lastName, String email, String password, String picture, Integer dni, Role role, Boolean alive, ArrayList<Book> listBook) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.picture = picture;
        this.dni = dni;
        this.role = role;
        this.alive = alive;
        this.listBook = listBook;
    }

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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getDni() {
        return dni;
    }

    public void setDni(Integer dni) {
        this.dni = dni;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getAlive() {
        return alive;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }

    public ArrayList<Book> getListBook() {
        return listBook;
    }

    public void setListBook(ArrayList<Book> listBook) {
        this.listBook = listBook;
    }

    
    
}
