package com.bibloteca.beta.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;



@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"customer_id", "book_id"})})//no puede existir un cartItem con el mismo customer y el mismo book
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem implements Serializable{
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;
    
    @ManyToOne
    private Book book;
    
    @ManyToOne
    private Customer customer;
    
    private Integer quantity;
    
    @Override
    public String toString() {
        return "Cart{" +
                ", nameBook='" + book.getName() + '\'' +
                ", cameCustomer" +customer.getName()+ '\'' +
                ", quantity='" + quantity ;
    }
}
