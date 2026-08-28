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
    public Photo findById(String id) throws Exception {
        Photo photo = photoRepository.findById(id).orElseThrow(() -> new Exception("no se encontro photo")); //nunca devuelve null
        if (photo == null || photo.getContent() == null) {
            throw new Exception("La foto no fue encontrada");
        }
        return photo;
    }

    @Transactional//tendria que ver si son nesesarias tanto findById como getOne
    public Photo getOne(String id) throws Exception {

        Photo photo = photoRepository.findById(id).orElseThrow(() -> new Exception("Foto no encontrada"));
        if (photo == null || photo.getContent() == null) {
            throw new Exception("La foto no fue encontrada");
        }
        return photo;
                
    }
}
