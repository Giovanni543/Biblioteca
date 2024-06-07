package com.bibloteca.beta.controllers;

import com.bibloteca.beta.entities.Author;
import com.bibloteca.beta.entities.Book;
import com.bibloteca.beta.services.AuthorService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
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
@RequestMapping("/author")
public class AuthorController {

    private AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public String ListAuthors(ModelMap model) {
        List<Author> authors = authorService.getAll();
        model.addAttribute("authors", authors);
        return "/author/list-authors.html";
    }

    @GetMapping("/form")
    public String ShowForm(ModelMap model, @RequestParam(required = false) String id){
        try {
            if (id == null) {
                model.addAttribute("author", new Author());
                return ("author/form");
            } else {
                Author author = authorService.findById(id);
                model.addAttribute("author", author);
                return ("author/form");
            }
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return ("author/form");
        }
    }

    @PostMapping("/form")
    public String SaveAuthor(@ModelAttribute Author author, ModelMap model, RedirectAttributes attr){//sobre el ModelMap
        try {
            ArrayList<Book> books = new ArrayList<>();
            author.setListBook(books);
            authorService.saveNew(author);
            return "/author/profile";
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
            System.out.println("Exception en controlador: " + e.getMessage());
            return "redirect:/author/form";
        }
    }

    @GetMapping("/profile")
    public String profile(ModelMap model, HttpSession http){
        try {
            Author author = (Author) http.getAttribute("authorsession");
            model.addAttribute("author", author);
            return "/author/profile";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "redirect:";
        }
    }

    @GetMapping("/addBook")//hacer vista addBook de ultima y ver
    @PreAuthorize("hasAnyRole('ROLE_AUTHOR')")
    public String addBook(HttpSession http, ModelMap model, Author author) {
        try {
            author = (Author) http.getAttribute("authorsession");//capaz que no haga falta en el get
            model.addAttribute("book", new Book());
            model.addAttribute("author", author);
            return "/book/addBook";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "/book/addBook";
        }
    }

    @PostMapping("/addBook")
    @PreAuthorize("hasAnyRole('ROLE_AUTHOR')")
    public String saveBook(HttpSession http, @ModelAttribute Author author, @ModelAttribute Book book, RedirectAttributes attr){
        try {
            author = (Author) http.getAttribute("authorsession");
            authorService.assignBook(book, author);
            System.out.println("libro con autor guardado");
            return "redirect:/book";
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
            System.out.println("Excepcion en controlador autor: " + e.getMessage());
            return "redirect:/book/addBook";
        }

    }

}
