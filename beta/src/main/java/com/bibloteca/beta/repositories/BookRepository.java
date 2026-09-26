package com.bibloteca.beta.repositories;

import com.bibloteca.beta.entities.Book;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    @Query("SELECT b FROM Book b WHERE b.name LIKE :name")
    public List<Book> SearchByName(@Param("name") String name);

    @Query("SELECT b FROM Book b ORDER BY name")//Trae lista de todos los libros organizados por nombre
    public List<Book> getAllOrganized();

    @Query("SELECT si.book "
            + "FROM SaleItem si "
            + "GROUP BY si.book "
            + "ORDER BY SUM(si.quantity) DESC")
    List<Book> findBestSellingBooks(Pageable pageable);

    @Query("SELECT b "
            + "FROM Book b "
            + "WHERE b NOT IN :excluded "
            + "ORDER BY b.publicationDate DESC")
    List<Book> findLatestBooksExcluding(
            @Param("excluded") List<Book> excluded,
            Pageable pageable);
    
    List<Book> findByNameContainingIgnoreCase(String name);

}
