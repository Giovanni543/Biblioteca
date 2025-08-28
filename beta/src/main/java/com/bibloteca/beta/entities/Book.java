/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibloteca.beta.entities;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book implements Serializable{//no van a haber 20 ojetos del libro x, va a haber un objeto del libro x con 20 de "stock"

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;//ISBN

    private String category;
    private String name;
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Photo photo;
    //private String backCover;
    //private String authorFullName;
    private Integer stock;
    private Integer pages;
    private Double price;
    private Boolean active;

    @ManyToOne
    private Author author;
    //@ManyToOne   ver como seria el tema del stock si un atributo del libro(unidades disponibles) o de la libreria(stock de tal libro)
    //private Library library;
    //private String description???
}