package com.bibloteca.beta.services;

import com.bibloteca.beta.entities.Book;
import com.bibloteca.beta.repositories.BookRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    
    private BookRepository bookRepository;
    
    @Autowired
    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }
    
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Exception.class)
    public void save (Book book)throws Exception{
        //Author author = authorService.findOrCreate(book.getAuthor().getName(), book.getAuthor().getLastName());
        //book.setAuthor(author);
        //System.out.println("Se asigno un autor al libro");
        activateDeactivate(book);
        
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
        Book book = bookRepository.getById(id);
        if(book == null){
            throw new Exception("No se encontro al libro con ese Id");
        }
        return book;
    }
    
    @Transactional
    public List<Book> findByName(String name)throws Exception{
        List<Book> books = bookRepository.SearchByName(name);
        if(books == null){
            throw new Exception("No se encontro a ningún libro con ese nombre");
        }
        return books;
    }
    
    @Transactional
    public List<Book> getAll(){
        return bookRepository.getAllOrganized();
    }
    
    public void validate(Book book)throws Exception{
        if(book.getName().isEmpty() || book.getName().length() < 5 || book.getName().equals(" ") || book.getName() == null){//poner en la vista las condiciones minimas para publicar un libro
            throw new Exception("El nombre ingresado es inválido");//poner obligatorio la foto del libro tmb
        }
        if(book.getAuthor() == null){
            throw new Exception("El libro tiene que tener asignado un autor que lo haya publicado");
        }
        if(book.getCategory().isEmpty() || book.getCategory().length() < 5 || book.getCategory().equals(" ") || book.getCategory() == null){
            throw new Exception("Categoria ingresada es inválida");
        }
        if(book.getStock() < 5 || book.getStock() == null){
            throw new Exception("El número de stock ingresado es inválido");
        }
        if(book.getPages() < 10 || book.getPages() == null){
            throw new Exception("El número de páginas ingresadas es inválida");
        }
        if(book.getPrice() < 5 || book.getPrice() == null || book.getPrice().toString().isEmpty() || book.getPrice().toString().equals(" ")){
            throw new Exception("El precio de venta ingresado es inválido");
        }
        System.out.println("Paso validación");
    }
}
