/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibloteca.beta.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public class Sale implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate saleDate;

    @ManyToOne
    //@JoinColumn(name = "customer_id")
    private Customer customer;

    //@ManyToMany//Con @OneToMany Hibernate interpretará que un libro pertenece a una única venta, lo cual no es correcto para una librería.
    //private List<Book> books = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();

    private Double totalAmount;

    /*public void addBook(Book book) {
        this.books.add(book);
    }*/
    @Override
    public String toString() {
        return "{id='" + id + '\''
                + ", saleDate='" + saleDate + '\''
                + ", customerName='" + customer.getName() + '\''
                + ", listBooks='" + cartItems.toString()
                + ", total amount='" + totalAmount
                + // ⚠️ No ponemos password ni photo
                '}';
    }
}
