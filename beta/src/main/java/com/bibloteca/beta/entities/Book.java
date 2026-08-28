package com.bibloteca.beta.entities;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

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
    private String description;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate publicationDate;

    @ManyToOne
    //@JoinColumn(name = "author_id")
    private Author author;
    
    
    @Override
    public String toString() {
        return 
                "{id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", stock='" + stock +
                ", pages='"+ pages+
                ", price='"+ price +
                // ⚠️ No ponemos password ni photo
                '}';
    }
}