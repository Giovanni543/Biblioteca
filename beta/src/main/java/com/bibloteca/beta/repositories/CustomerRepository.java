
package com.bibloteca.beta.repositories;

import com.bibloteca.beta.entities.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository <Customer, String> {
    
    @Query("SELECT c FROM Customer c WHERE c.name LIKE :name")
    public List<Customer> SearchByName(@Param("name") String name);
    
    @Query("SELECT c FROM Customer c ORDER BY name")//Trae lista de todos los autores organizados por nombre
    public List<Customer> getAllOrganized();

    @EntityGraph(attributePaths = "photo")  
    @Query("SELECT c FROM Customer c WHERE c.email LIKE :email")
    public Customer findByEmail(@Param("email") String email);
    
}