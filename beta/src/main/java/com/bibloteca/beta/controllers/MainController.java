package com.bibloteca.beta.controllers;

import com.bibloteca.beta.entities.Book;
import com.bibloteca.beta.entities.Customer;
import com.bibloteca.beta.services.BookService;
import com.bibloteca.beta.services.CustomerService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class MainController {

    private BookService bookService;
    
    @Autowired
    public MainController(BookService bookService){
        this.bookService = bookService;
    }

    @GetMapping
    public String index(ModelMap model) {
        List<Book> books = bookService.getBooksForIndex();
        model.addAttribute("books", books);
        
        return "index.html";
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error, ModelMap model) {

        if (error != null) {
            model.put("error", "El Email o contraseña fueron ingresados incorrectamente");
        }
        return "login";
    }

    @GetMapping("/signUp")
    public String registrationForm(ModelMap model) {//solo para customer?? y para autor??(como pasa en el securityConfig)
        model.addAttribute("customer", new Customer());
        return "customer/form";
    }

    @GetMapping("/registerRole")
    public String registrationRole(ModelMap model) {
        return "registerRole";
    }
}
