package com.bibloteca.beta.controllers;

import com.bibloteca.beta.entities.Book;
import com.bibloteca.beta.services.BookService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/book")
public class BookController {

    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String showBooks(ModelMap model) {
        List<Book> books = bookService.getAll();
        model.addAttribute("books", books);
        return "/book/list-books.html";
    }

    @GetMapping("/form")
    @PreAuthorize("hasAnyRole('ROLE_AUTHOR')")
    public String showForm(ModelMap model, @RequestParam(required = false) String id){
        try {
            if (id == null) {
                model.addAttribute("book", new Book());
                System.out.println("get de form");
                return "/book/addBook";
            } else {
                Book book = bookService.findById(id); 
                model.addAttribute("book", book);
                return "/book/addBook";
            }
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "/book/addBook";
        }
    }
    
    @PostMapping("/form")
    @PreAuthorize("hasAnyRole('ROLE_AUTHOR')")
    public String saveBook(@ModelAttribute Book book,RedirectAttributes attr){
        try{
            System.out.println("post de form");
            bookService.save(book);//capaz tenga que llamar aca al servicio de autor
            System.out.println("libro se ah guardado :)");
            return "redirect:/book";
        }catch(Exception e){
            attr.addFlashAttribute("error", e.getMessage());
            System.out.println("Excepcion en controlador libro: "+ e.getMessage());//falta que tire los msj de error en algunos atriutos y que en caso de error, vuelva a recargar la pag con los atributos anteriores 
            return "redirect:/book/addBook";
        }
    }
}
