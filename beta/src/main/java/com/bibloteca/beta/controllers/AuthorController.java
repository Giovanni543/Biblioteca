package com.bibloteca.beta.controllers;

import com.bibloteca.beta.entities.Author;
import com.bibloteca.beta.entities.Book;
import com.bibloteca.beta.entities.Photo;
import com.bibloteca.beta.services.AuthorService;
import com.bibloteca.beta.services.BookService;
import com.bibloteca.beta.services.PhotoService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/author")
public class AuthorController {

    private AuthorService authorService;
    private BookService bookService;
    private PhotoService photoService;

    @Autowired
    public AuthorController(AuthorService authorService, BookService bookService, PhotoService photoService) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.photoService = photoService;
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
    public String SaveAuthor(@ModelAttribute Author author,@RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String newPassword, ModelMap model, RedirectAttributes attr){//sobre el ModelMap
        try {
            if(author.getId() == null){
                List<Book> books = new ArrayList<>();
                author.setListBook(books);
                authorService.saveNew(author);
                System.out.println("se instanció y guardo el arraylist del autor");
            }else{
                authorService.update(author, file, newPassword);
            }
            
            //ArrayList<Book> books = new ArrayList<>();//En servicio
            //author.setListBook(books);
            System.out.println("se instanció y guardo el arraylist del autor");
            System.out.println("Author guardado :)");
            return "redirect:/author/profile";
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
    
    @GetMapping("/edit-profile")
    public String editGet(ModelMap model, HttpSession http){
        try{
            Author author = (Author) http.getAttribute("authorsession");
            model.addAttribute("author", author);
            System.out.println("author get"+ author.toString());
            return "/author/edit-profile";
        }catch(Exception e){
            model.put("error", e.getMessage());
            return "author/profile";
        }
    }
    
    @PostMapping("/edit-profile")
    public String editPost(@ModelAttribute Author author, @RequestParam("photoFile") MultipartFile file, @RequestParam(value = "newPassword", required =false)String newPassword, RedirectAttributes attr, HttpSession http){
        try{
            //authorService.update(author, file, newPassword);
            Author actualizado = authorService.update(author, file, newPassword);
            http.setAttribute("authorsession", actualizado);
            attr.addFlashAttribute("succed", "edit del perfil de author EXITOSO");
            return "redirect:/logout";
        }catch(Exception e){
            attr.addFlashAttribute("error", e.getMessage());
            return "/index";
        }
    }

    @GetMapping("/addBook")//hacer vista addBook de ultima y ver
    @PreAuthorize("hasAnyRole('ROLE_AUTHOR')")
    public String addBook(HttpSession http, ModelMap model) {
        try {
            Author author = (Author) http.getAttribute("authorsession");//capaz que no haga falta en el get
            model.addAttribute("book", new Book());
            model.addAttribute("author", author);
            System.out.println("se logro el get");
            return "/book/addBook";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            System.out.println("todo mal");
            return "/book/addBook";
        }
    }

    @PostMapping("/addBook")
    @PreAuthorize("hasAnyRole('ROLE_AUTHOR')")
    public String saveBook(HttpSession http, @ModelAttribute Book book, RedirectAttributes attr, @RequestParam("photoFile") MultipartFile file){
        try {
            System.out.println("entro en el post");
            Author author = (Author) http.getAttribute("authorsession");
            Photo photo = photoService.save(file);
            
            book.setPhoto(photo);
            
            System.out.println(author.toString());
            System.out.println(author.getId());
            if(author.getListBook() == null){
                System.out.println("La lista esta en null");
            }else{
                System.out.println("lista no vacia");
            }
            
            authorService.assignBook(book, author.getId());
            System.out.println("libro con autor guardado");
            return "redirect:/book";
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
            System.out.println("Excepcion en controlador autor: " + e.getMessage());
            return "redirect:/book/addBook";
        }
    }
    
    @GetMapping("/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable String id) throws Exception {

        Photo photo = photoService.findById(id);//preguntar por este procedimiento porque lo tengo que hacer optional y no photo

        if (photo != null ) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(photo.getMime()));
            return new ResponseEntity<>(photo.getContent(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
