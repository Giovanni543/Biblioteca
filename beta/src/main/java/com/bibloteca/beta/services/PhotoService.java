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
    public Photo update(Photo oldPhoto, MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            Photo photo;

            // Si ya había una, actualizar la misma
            if (oldPhoto != null) {
                photo = oldPhoto;
            } else {
                photo = new Photo();
            }

            photo.setMime(file.getContentType());
            photo.setName(file.getOriginalFilename());
            photo.setContent(file.getBytes());

            return photoRepository.save(photo);
        }
        return oldPhoto; // si no se subió nada, devolver la misma
    }

    @Transactional
    public Optional<Photo> findById(String id) throws Exception {
        Optional<Photo> photo = photoRepository.findById(id); //nunca devuelve null
        if (!photo.isPresent()) {
            throw new Exception("La foto no fue encontrada");
        }
        return photo;
    }
}
