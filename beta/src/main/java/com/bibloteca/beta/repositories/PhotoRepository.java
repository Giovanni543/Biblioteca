
package com.bibloteca.beta.repositories;

import com.bibloteca.beta.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public interface PhotoRepository extends JpaRepository <Photo, String> {

    //public Photo save(MultipartFile archivo);
    
}
