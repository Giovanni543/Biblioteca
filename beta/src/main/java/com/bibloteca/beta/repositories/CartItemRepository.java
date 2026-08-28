package com.bibloteca.beta.repositories;

import com.bibloteca.beta.entities.Book;
import com.bibloteca.beta.entities.Customer;
import com.bibloteca.beta.entities.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    
    List<CartItem> findByCustomer(Customer customer);
    
    Optional<CartItem> findByCustomerAndBook(Customer customer, Book book);
    
    void deleteByCustomer(Customer customer);
    
}
