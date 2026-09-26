package com.bibloteca.beta.services;

import com.bibloteca.beta.entities.Book;
import com.bibloteca.beta.entities.CartItem;
import com.bibloteca.beta.entities.Customer;
import com.bibloteca.beta.entities.Photo;
import com.bibloteca.beta.entities.Sale;
import com.bibloteca.beta.entities.SaleItem;
import com.bibloteca.beta.enums.Role;
import java.util.List;
import java.util.ArrayList;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.bibloteca.beta.repositories.CustomerRepository;
import com.bibloteca.beta.repositories.SaleRepository;
import java.time.LocalDateTime;
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
public class CustomerService implements UserDetailsService {

    private CustomerRepository customerRepository;
    private PhotoService photoService;
    private BookService bookService;
    private SaleRepository saleRepository;
    private CartItemService cartItemService;
    private final PasswordEncoder passwordEncoder;
    //private SaleRepository

    @Autowired//la inyeccion de dependencia en los constructores nos permite hacer tessting despues de manera mas sencilla
    public CustomerService(CustomerRepository customerRepository, PhotoService photoService, BookService bookService, SaleRepository saleRepository, CartItemService cartItemService, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.photoService = photoService;
        this.bookService = bookService;
        this.saleRepository = saleRepository;
        this.cartItemService = cartItemService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Exception.class)
    public void saveNew(Customer customer) throws Exception {

        activateIfNew(customer);
        validate(customer);

        System.out.println("paso la validacion y el activado");

        String passwordEncripted = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(passwordEncripted);
        System.out.println("hasta aca todo bien");
        customerRepository.save(customer);
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Exception.class)
    public Customer update(Customer customer, MultipartFile file, String newPassword) throws Exception {
        System.out.println("entro al servicio");
        Customer principal = customerRepository.findById(customer.getId())//customer es el objeto solo con los atributos modificados, principal es el objeto traído de la bbdd
                .orElseThrow(() -> new RuntimeException("Autor no encontrado"));

        System.out.println(customer.toString());
        System.out.println(principal.toString());
        System.out.println(customer.getPassword());
        System.out.println(newPassword);

        principal.setName(customer.getName()); // total de campos que se permiten modificar: 6
        principal.setLastName(customer.getLastName());
        principal.setEmail(customer.getEmail());
        principal.setDni(customer.getDni());

        // Actualizar foto (el photoService decide si crea o actualiza)
        if (file != null && !file.isEmpty()) {
            System.out.println("actualizo foto");
            Photo newPhoto = photoService.update(principal.getPhoto(), file);
            principal.setPhoto(newPhoto);
        }

        if (newPassword != null && !newPassword.isEmpty()) {//encripta nuevamente la contraseña si es que se ingresó una nueva
            System.out.println("cambio de contraseña");
            principal.setPassword(passwordEncoder.encode(newPassword));
        }
        validate(principal);
        System.out.println("kkk");
        return customerRepository.save(principal);
    }
    
    @Transactional
    public Customer addBalance(Customer customer, Double amount) throws Exception{
        Customer principal = customerRepository.findById(customer.getId()).orElseThrow(() -> new Exception("No se encontró el usuario"));
        
        if(principal.getBalance() == null){
            principal.setBalance(0.0);
        }
        
        principal.setBalance(principal.getBalance() + amount);
        return customerRepository.save(principal);
    }

    private void activateIfNew(Customer customer) throws Exception {
        if (customer.getActive() == null || customer.getActive().equals(false)) {
            customer.setActive(Boolean.TRUE);
            customer.setRole(Role.USER);
        }
    }

    @Transactional
    public Customer findById(String id) throws Exception {
            return customerRepository.findById(id)
            .orElseThrow(() ->
                new Exception("No se encontró al usuario con ese Id")
            );
    }

    @Transactional
    public List<Customer> findByName(String name) throws Exception {
        List<Customer> customers = customerRepository.SearchByName(name);
        if (customers == null) {
            throw new Exception("no se encontro al usuario con ese nombre");
        }
        return customers;
    }

    @Transactional
    public Customer findyEmail(String email) throws Exception {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            throw new Exception("No se encontro a ningun usuario con dicho email");
        }
        return customer;
    }

    @Transactional
    public List<Customer> getAll() {
        return customerRepository.getAllOrganized();
    }

    private void validate(Customer customer) throws Exception {
        if (customer.getName() == null || customer.getName().isEmpty() || customer.getName().length() < 3 || customer.getName().equals(" ")) {
            throw new Exception("El nombre ingresado es invalido");
        }
        if (customer.getLastName() == null || customer.getLastName().equals(" ") || customer.getLastName().isEmpty() || customer.getLastName().length() < 3) {
            throw new Exception("El apellido ingreado es invalido");
        }
        if (customer.getEmail() == null || customer.getEmail().isEmpty() || customer.getEmail().equals(" ") || customer.getEmail().length() < 8) {
            throw new Exception("El email ingresado es invalido");
        }
        if (customer.getPassword() == null || customer.getPassword().isEmpty() || customer.getPassword().equals(" ") || (customer.getPassword().length() < 8)) {
            throw new Exception("La contraseña ingresada es invalida");
        }//si el dni ingresado es menor a 10 millones o mayor a 90 millones se tiene como valor erroneo
        if (customer.getDni() < 10000000 || customer.getDni() > 90000000 || customer.getDni() == null || customer.getDni().toString().isEmpty() || customer.getDni().toString().equals(" ")) {
            throw new Exception("El DNI ingreado es invalido");
        }
        //validar rol y active

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            throw new UsernameNotFoundException("usuario no encontrado " + email);
        }

        List<GrantedAuthority> permissions = new ArrayList<>();
        GrantedAuthority rolePermissions = new SimpleGrantedAuthority("ROLE_" + customer.getRole().toString());
        permissions.add(rolePermissions);

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        session.setAttribute("customersession", customer);

        return new User(customer.getEmail(), customer.getPassword(), permissions);
    }

    @Transactional
    public void buyBooks(Customer customer, List<CartItem> items) throws Exception {
        
        if (items == null || items.isEmpty()) {
            throw new Exception("No se seleccionaron libros");
        }
        Double total = 0.0;

        for (CartItem item : items) {//Valida si hay stock
            Book book = item.getBook();
            
            if(book.getStock() < item.getQuantity()){
                throw new Exception("Stock insuficiente para: "+ book.getName());
            }
            total += book.getPrice() * item.getQuantity();
            
        }
        
        if (customer.getBalance() < total) {//Valido el saldo
            throw new Exception("Saldo insuficiente");
        }
        
        Sale sale = new Sale();

        sale.setCustomer(customer);
        sale.setSaleDate(LocalDateTime.now());
        System.out.println("Fecha y hora en la que se va a guardar :"+ sale.getSaleDate());
        sale.setTotalAmount(total);
        
        for(CartItem item : items){
            
            SaleItem saleItem = new SaleItem();
            
            saleItem.setBook(item.getBook());
            saleItem.setQuantity(item.getQuantity());
            saleItem.setPrice(item.getBook().getPrice());
            saleItem.setSale(sale);
            
            sale.getSaleItems().add(saleItem);
        }

        customer.setBalance(customer.getBalance() - total);

        for (CartItem item : items) {//resta unidades disponibles (stock)
            Book book = item.getBook();
            book.setStock(book.getStock() - item.getQuantity());
            bookService.save(book);
        }
        
        System.out.println("Antes de agregar venta");
        saleRepository.save(sale);
        customerRepository.save(customer);
        
        System.out.println("despues de agregar venta");
    }
    
    @Transactional
    public void buyBook(Customer customer, String id, Integer quantity)throws Exception{
        
        Book book = bookService.findById(id);
        
        CartItem item = new CartItem();
        
        item.setBook(book);
        item.setCustomer(customer);
        item.setQuantity(quantity);
        
        List<CartItem> items = new ArrayList<>();
        items.add(item);
        
        buyBooks(customer, items);
    }
}
