package com.bibloteca.beta.services;

import com.bibloteca.beta.entities.Author;
import com.bibloteca.beta.entities.Book;
import com.bibloteca.beta.enums.Role;
import java.util.List;
import java.util.ArrayList;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.bibloteca.beta.repositories.AuthorRepository;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuthorService implements UserDetailsService {

    private AuthorRepository authorRepository;
    private BookService bookService;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, BookService bookService) {
        this.authorRepository = authorRepository;
        this.bookService = bookService;
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Exception.class)
    public void saveNew(Author author) throws Exception {

        isAlive(author);//cambiar por si el autor está vivo a isAlive o ver como hacer qsy
        asignRole(author);
        validate(author);

        String passwordEncripted = new BCryptPasswordEncoder().encode(author.getPassword());
        author.setPassword(passwordEncripted);
        authorRepository.save(author);
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Exception.class)
    public void save(Author author) throws Exception {

        authorRepository.save(author);
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

    private void asignRole(Author author) {
        if (author.getRole() == null) {
            author.setRole(Role.AUTHOR);
        }
    }

    private void validate(Author author) throws Exception {
        if (author.getName() == null || author.getName().isEmpty() || author.getName().equals(" ") || author.getName().length() < 3) {
            throw new Exception("El nombre ingresado en inválido");
        }
        if (author.getLastName() == null || author.getLastName().isEmpty() || author.getLastName().equals(" ") || author.getLastName().length() < 3) {
            throw new Exception("El apellido ingresado en inválido");
        }
        if (author.getRole() == null) {
            throw new Exception("El autor a crear no cuenta con el rol de AUTHOR");
        }
        if (author.getAlive() != null && author.getAlive() == true) {//si el autor está vivo tiene que presentar los siguentes atributos

            if (author.getDni() == null || author.getDni() < 10000000 || author.getDni() > 90000000 || author.getDni().toString().isEmpty() || author.getDni().toString().equals(" ")) {
                throw new Exception("El apellido ingresado en inválido");
            }

            if (author.getEmail() == null || author.getEmail().isEmpty() || author.getEmail().equals(" ") || author.getEmail().length() < 8) {
                throw new Exception("El apellido ingresado en inválido");
            }

            if (author.getPassword() == null || author.getPassword().isEmpty() || author.getPassword().equals(" ") || author.getPassword().length() < 8) {
                throw new Exception("El apellido ingresado en inválido");
            }
        }
    }

    @Transactional
    public void assignBook(Book book, Author author) throws Exception {
        book.setAuthor(author);
        //book.setAuthorFullName(author.getFullName());
        
        bookService.validate(book);
        bookService.save(book);
        
        ArrayList<Book> books = author.getListBook();
        
        books.add(book);
        //author.setListBook(books);????
        System.out.println("Se agrego el libro a la lista en el índice " + books.size());

        int i = 1;
        for (Book book1 : books) {
            System.out.println(book1.toString());
            System.out.println("iteración número " + i);
            i++;
        }
        author.setListBook(books);
        save(author);

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

    /*@Transactional
    public Author findByFullName(String name, String last_name)throws Exception{
        Author author = authorRepository.searchByFullName(name, last_name);
        System.out.println("volvió del repositorio");
        if(author == null){
            System.out.println("No se encontro ningún autor con dicho nombre y apellido en la base de datos");
        }
        return author;
    }
    
    @Transactional
    public Author findOrCreate(String name, String lastName)throws Exception{
        Author author = findByFullName(name, lastName);
        if(author == null){
            author = new Author();
            author.setName(name);
            author.setLastName(lastName);
            asignRole(author);
            isNotAlive(author);//para confirmar que un autor esta vivo ver como lo puedo crear con todos los atriutos//puedo poner un boton para editar(validar)
            System.out.println("se asigno atributos a un nuevo autor");
        }
        return author;
    }*/
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
}
