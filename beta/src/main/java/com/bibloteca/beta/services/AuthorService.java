package com.bibloteca.beta.services;

import com.bibloteca.beta.entities.Author;
import com.bibloteca.beta.entities.Book;
import com.bibloteca.beta.entities.Photo;
import com.bibloteca.beta.enums.Role;
import java.util.List;
import java.util.ArrayList;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.bibloteca.beta.repositories.AuthorRepository;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AuthorService implements UserDetailsService {

    private AuthorRepository authorRepository;
    private BookService bookService;
    private PhotoService photoService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, BookService bookService, PasswordEncoder passwordEncoder, PhotoService photoService) {
        this.authorRepository = authorRepository;
        this.bookService = bookService;
        this.passwordEncoder = passwordEncoder;
        this.photoService = photoService;
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Exception.class)
    public void saveNew(Author author) throws Exception {

        validate(author);
        activateIfNew(author);
        
        System.out.println("Paso validación y activado");

        String passwordEncripted = passwordEncoder.encode(author.getPassword());
        author.setPassword(passwordEncripted);
        authorRepository.save(author);
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Exception.class)
    public Author update(Author author, MultipartFile file, String newPassword) throws Exception {//hacer funcion solo para el cambio de contra

        Author principal = authorRepository.findById(author.getId()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        System.out.println("1 " + author.toString());
        System.out.println("2 " + principal.toString());

        System.out.println(author.getName());
        principal.setName(author.getName());
        System.out.println(principal.getName());
        principal.setLastName(author.getLastName());
        principal.setEmail(author.getEmail());
        principal.setDni(author.getDni());
        System.out.println("#: "+ principal.toString());
        

        if (file != null && !file.isEmpty()) {
            Photo newPhoto = photoService.update(principal.getPhoto(), file);
            principal.setPhoto(newPhoto);
            System.out.println("Actualizo foto");
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            System.out.println("actualiza contraseña");
            principal.setPassword(passwordEncoder.encode(newPassword));
        }
        validate(principal);

        System.out.println(principal.getName());
        return authorRepository.save(principal);
    }

    private void activateIfNew(Author author) throws Exception {
        if (author.getAlive() == null || author.getAlive().equals(false)) {
            author.setAlive(Boolean.TRUE);
            author.setRole(Role.USER);
        }
    }

    private void validate(Author author) throws Exception {
        if (author.getName() == null || author.getName().isEmpty() || author.getName().equals(" ") || author.getName().length() < 3 || author.getName().trim().isEmpty()) {
            throw new Exception("El nombre ingresado es inválido");
        }
        if (author.getLastName() == null || author.getLastName().isEmpty() || author.getLastName().equals(" ") || author.getLastName().length() < 3 || author.getLastName().trim().isEmpty()) {
            throw new Exception("El apellido ingresado es inválido");
        }
        if (author.getRole() == null) {
            throw new Exception("El autor a crear no cuenta con el rol de AUTHOR");
        }
        if (author.getAlive() != null && author.getAlive() == true) {//si el autor está vivo tiene que presentar los siguentes atributos

            if (author.getDni() == null || author.getDni() < 10000000 || author.getDni() > 90000000 || author.getDni().toString().isEmpty() || author.getDni().toString().equals(" ")) {
                throw new Exception("El DNI ingresado es inválido");
            }

            if (author.getEmail() == null || author.getEmail().isEmpty() || author.getEmail().equals(" ") || author.getEmail().length() < 8) {
                throw new Exception("El Email ingresado esinválido");
            }

            if (author.getPassword() == null || author.getPassword().isEmpty() || author.getPassword().equals(" ") || author.getPassword().length() < 8 || author.getPassword().trim().isEmpty()) {
                throw new Exception("La contraseña ingresada es inválida");
            }
        }
    }

    @Transactional
    public void assignBook(Book book, String authorID) throws Exception {
        
        Author authorDB = authorRepository.findById(authorID).orElseThrow(() -> new Exception("Author no encontrado"));
        book.setAuthor(authorDB);
        
        bookService.save(book);
        System.out.println("Servicio de author " + book.toString());
        System.out.println("lista: "+ authorDB.getListBook());
        List<Book> books = authorDB.getListBook();

        System.out.println("qqq");
        books.add(book);
        //author.setListBook(books);????
        System.out.println("Se agrego el libro a la lista en el índice " + books.size());

        int i = 1;
        for (Book book1 : books) {
            System.out.println(book1.toString());
            System.out.println("iteración número " + i);
            i++;
        }
        authorDB.setListBook(books);
        authorRepository.save(authorDB);

    }

    @Transactional
    public Author findById(String id) throws Exception {
        Author author = authorRepository.getById(id);
        if (author == null) {
            throw new Exception("El autor a buscar no se encontro con ese id");
        }
        return author;
    }

    @Transactional
    public List<Author> findByName(String name) throws Exception {
        List<Author> authors = authorRepository.SearchByName(name);
        if (authors == null) {
            throw new Exception("No se encontro a ningun author con ese nombre");
        }
        return authors;
    }

    @Transactional
    public Author findByEmail(String email) throws Exception {
        Author author = authorRepository.findByEmail(email);
        if (author == null) {
            throw new Exception("No se encontró a un autor con dicho email");
        }
        return author;
    }

    private void isAlive(Author author) {
        if (author.getAlive() == null) {
            author.setAlive(Boolean.TRUE);
        }
    }

    private void isNotAlive(Author author) {//Si el autor no esta vivo quien crea su cuenta? un admin? para eso ponemos por determinado que este vivo si se crea desde la pag web
        if (author.getAlive() == null || author.getAlive().equals(true)) {
            author.setAlive(Boolean.FALSE);
        }
    }

    private void onnOff(Author author) {
        if (author.getAlive() == false) {
            author.setAlive(Boolean.TRUE);
        }
        if (author.getAlive() == true) {
            author.setAlive(Boolean.FALSE);
        }
    }

    @Transactional
    public List<Author> getAll() {
        return authorRepository.getAllOrganized();
    }

    @Transactional
    public List<Author> getAllAlive() {
        return authorRepository.FindAllAlive(Boolean.TRUE);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Author author = authorRepository.findByEmail(email);
        if (author == null) {
            throw new UsernameNotFoundException("autor no encontrado");
        }

        List<GrantedAuthority> permissions = new ArrayList<>();
        GrantedAuthority rolePermissions = new SimpleGrantedAuthority("ROLE_" + author.getRole().toString());
        permissions.add(rolePermissions);

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpSession session = attr.getRequest().getSession(true);

        
        session.setAttribute("authorsession", author);

        return new User(author.getEmail(), author.getPassword(), permissions);
    }
    //restore workspace
    //could not read contents of "C:/users/giova/AppData/Roaming/MySQL/Worckbench\"
    //failed to open file "C:/users/giova/AppData/Roaming/MySQL/Worckbench\"
    //no such file or directory
}
