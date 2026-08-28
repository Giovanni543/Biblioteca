package com.bibloteca.beta.repositories;

import com.bibloteca.beta.entities.Sale;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SaleRepository extends JpaRepository <Sale, String> {
    
    @Query("SELECT s FROM Sale s ORDER BY saleDate")//Trae lista de todos los libros organizados por nombre
    public List<Sale> getAllOrganized();
}
