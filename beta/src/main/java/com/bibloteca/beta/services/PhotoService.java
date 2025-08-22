package com.bibloteca.beta.services;

import com.bibloteca.beta.entities.Photo;
import com.bibloteca.beta.repositories.PhotoRepository;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    //Convierte el archivo en bytes y lo guarda como objeto Photo con sus metadatos (nombre, tipo MIME, contenido).
    @Transactional
    public Photo save(MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            Photo photo = new Photo();
            photo.setMime(file.getContentType());
            photo.setName(file.getOriginalFilename());
            photo.setContent(file.getBytes());

            return photoRepository.save(photo);
        }
        return null;
    }

    @Transactional
    public Optional<Photo> findById(String id) throws Exception {
        Optional<Photo> photo = photoRepository.findById(id);
        if (photo == null) {
            throw new Exception("La foto no fue encontrada");
        }
        return photo;
    }
}
