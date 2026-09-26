package com.bibloteca.beta.services;

import com.bibloteca.beta.entities.Book;
import com.bibloteca.beta.repositories.BookRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BookService {
    
    private BookRepository bookRepository;
    
    @Autowired
    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }
    
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Exception.class)
    public void save (Book book)throws Exception{

        System.out.println("Entro al servicio de libro");
        if(book.getPublicationDate() == null){
            book.setPublicationDate(LocalDateTime.now());
            System.out.println("Fecha añadida");
        }
        
        validate(book);
        activateDeactivate(book);
        
        System.out.println("libro: "+ book.toString());
        bookRepository.save(book);
    }
    
    private void activateDeactivate(Book book){
        if (book.getActive() == null || (book.getActive().equals(false) && book.getStock() > 0)) {
            book.setActive(Boolean.TRUE);
        }
        if(book.getStock() == 0 || book.getStock() == null){
            book.setActive(Boolean.FALSE);
        }
    }
    
    @Transactional
    public Book findById(String id)throws Exception{
        return bookRepository.findById(id).orElseThrow(() -> new Exception("No se encontró el libro"));
    }
    
    @Transactional
    public List<Book> findByName(String name)throws Exception{
        if(name == null || name.trim().isEmpty()){
            return bookRepository.findAll();
        }
        
        List<Book> books = bookRepository.findByNameContainingIgnoreCase(name);
        
        if(books == null){
            throw new Exception("No se encontro a ningún libro con ese nombre");
        }
        return books;
    }
    
    @Transactional
    public List<Book> findAllById(List<String> booksId) throws Exception{
        List<Book> books = bookRepository.findAllById(booksId);
        if(books == null){
            throw new Exception("no se encontro a ningun libro con dichos IDs");
        }
        return books;
    }
    
    @Transactional
    public List<Book> getAll(){
        return bookRepository.getAllOrganized();
    }
    
    public void validate(Book book)throws Exception{
        if(book.getName() == null || book.getName().isEmpty() || book.getName().length() < 4 || book.getName().equals(" ")){//poner en la vista las condiciones minimas para publicar un libro
            throw new Exception("El nombre ingresado es inválido");//poner obligatorio la foto del libro tmb
        }
        if(book.getAuthor() == null){
            throw new Exception("El libro tiene que tener asignado un autor que lo haya publicado");
        }
        if(book.getCategory() == null || book.getCategory().isEmpty() || book.getCategory().length() < 5 || book.getCategory().equals(" ")){
            throw new Exception("Categoria ingresada es inválida");
        }
        if(book.getStock() == null){
            throw new Exception("El número de stock ingresado es inválido");
        }
        if(book.getPages() == null || book.getPages() < 10){
            throw new Exception("El número de páginas ingresadas es inválida");
        }
        if(book.getPrice() == null || book.getPrice() < 5 || book.getPrice().toString().isEmpty() || book.getPrice().toString().equals(" ")){
            throw new Exception("El precio de venta ingresado es inválido");
        }
        System.out.println("Paso validación");
    }
    
    @Transactional
    public List<Book>getBooksForIndex(){
        
        int maxBooks = 8;
        Pageable pageable =PageRequest.of(0, maxBooks);
        
        List<Book> bestSelling = bookRepository.findBestSellingBooks(pageable);//Los libros mas vendidos
        
        if(bestSelling.size() >= maxBooks){//si estan los 8, retorno
            System.out.println("Se encontraron los 8 libros mas vendidos ");
            return bestSelling;
        }
        
        int remaining = maxBooks - bestSelling.size();
        
        Pageable remainingPageable = PageRequest.of(0, remaining);//Si faltan para llegar a 8, completo con libros publicados recientemente
        
        List <Book> latest = bookRepository.findLatestBooksExcluding(bestSelling, remainingPageable);
        
        bestSelling.addAll(latest);
        return bestSelling;
    }    
    
}
