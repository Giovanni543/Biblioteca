package com.bibloteca.beta.controllers;

import com.bibloteca.beta.entities.Book;
import com.bibloteca.beta.entities.Photo;
import com.bibloteca.beta.services.BookService;
import com.bibloteca.beta.services.PhotoService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/book")
public class BookController {

    private BookService bookService;
    private PhotoService photoService;

    @Autowired
    public BookController(BookService bookService, PhotoService photoService) {
        this.bookService = bookService;
        this.photoService = photoService;
    }

    @GetMapping
    public String showBooks(ModelMap model) {
        List<Book> books = bookService.getAll();
        model.addAttribute("books", books);
        return "/book/list-books.html";
    }

    @GetMapping("/form")
    @PreAuthorize("hasAnyRole('ROLE_AUTHOR')")
    public String showForm(ModelMap model, @RequestParam(required = false) String id) {
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
    public String saveBook(@ModelAttribute Book book, @RequestParam("archivo") MultipartFile file, RedirectAttributes attr) {
        try {
            System.out.println("post de form");
            bookService.save(book);//capaz tenga que llamar aca al servicio de autor
            System.out.println("libro se ah guardado :)");
            return "redirect:/book";
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
            System.out.println("Excepcion en controlador libro: " + e.getMessage());//falta que tire los msj de error en algunos atriutos y que en caso de error, vuelva a recargar la pag con los atributos anteriores 
            return "redirect:/book/addBook";
        }
    }

    @GetMapping("/vieww")
    //@PreAuthorize("hasAnyRole('ROLE_AUTHOR')")
    public String viewBook(@RequestParam String id, ModelMap model, RedirectAttributes attr) {//modelmap
        try {

            Book book = bookService.findById(id);
            //System.out.println(book.getName() + "  " + book.getId() + "  " + book.getCategory());
            model.addAttribute("book", book);
            System.out.println(book.toString());
            return "/book/vieww";
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
            System.out.println("Excepcion en controlador libro: " + e.getMessage());//falta que tire los msj de error en algunos atriutos y que en caso de error, vuelva a recargar la pag con los atributos anteriores 
            return "redirect:/book";
        }
    }

    @GetMapping("/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getPhoto(@PathVariable String id) {

        try {

            Photo photo = photoService.getOne(id);//en el controlador de author, para ver la foto de perfil uso findById

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, photo.getMime())
                    .body(photo.getContent());

        } catch (Exception e) {

            return ResponseEntity.notFound().build();

        }
    }
}
