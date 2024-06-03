package com.bibloteca.beta.repositories;

import com.bibloteca.beta.entities.Author;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthorRepository extends JpaRepository <Author, String>{
    
    @Query("SELECT a FROM Author a WHERE a.name LIKE: name")
    public List<Author> SearchByName(@Param("name")String name);
    
    @Query("SELECT a FROM Author a WHERE (a.name LIKE: name) AND (a.lastName LIKE: last_name)")
    public Author searchByFullName(@Param("name")String name, @Param("lastName")String last_name);
    
    @Query("SELECT a FROM Author a WHERE a.alive = True ORDER BY a.name")
    public List<Author> FindAllAlive(@Param("alive")Boolean name);
    //en SQL Server el tipo de dato booleano son en realidad bit cero(false) o uno(true)
    //ver si la sintaxis es correcta y devuelva la lista de autores activos
    
    @Query("SELECT a FROM Author a ORDER BY name")
    public List<Author> getAllOrganized();
    
    public Author findByEmail(String email);
}
