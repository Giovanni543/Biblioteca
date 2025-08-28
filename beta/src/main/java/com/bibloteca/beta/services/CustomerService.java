package com.bibloteca.beta.services;

import com.bibloteca.beta.entities.Customer;
import com.bibloteca.beta.entities.Photo;
import com.bibloteca.beta.enums.Role;
import java.util.List;
import java.util.ArrayList;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.bibloteca.beta.repositories.CustomerRepository;
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

    @Autowired//la inyeccion de dependencia en los constructores nos permite hacer tessting despues de manera mas sencilla
    public CustomerService(CustomerRepository customerRepository, PhotoService photoService) {
        this.customerRepository = customerRepository;
        this.photoService = photoService;
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Exception.class)
    public void saveNew(Customer customer) throws Exception {

        validate(customer);
        activateIfNew(customer);
        System.out.println("paso la validacion y el activado");

        //String passwordEncripted = passwordEncoder.encode(customer.getPassword());
        //customer.setPassword(passwordEncripted);
        String passwordEncripted = new BCryptPasswordEncoder().encode(customer.getPassword());
        customer.setPassword(passwordEncripted);
        System.out.println("hasta aca todo bien");
        customerRepository.save(customer);
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Exception.class)
    public void save(Customer customer, MultipartFile file, String newPassword) throws Exception {
        System.out.println("entro al servicio");
        Customer principal = customerRepository.findById(customer.getId())//customer es el objeto solo con los atributos modificados, principal es el objeto traído de la bbdd
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

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
            principal.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        }
        validate(principal);
        customerRepository.save(principal);
        System.out.println("kkk");
    }

    private void activateIfNew(Customer customer) throws Exception {
        if (customer.getActive() == null || customer.getActive().equals(false)) {
            customer.setActive(Boolean.TRUE);
            customer.setRole(Role.USER);
        }
    }

    @Transactional
    public Customer findById(String id) throws Exception {
        Customer customer = customerRepository.getById(id);//findById
        if (customer == null) {
            throw new Exception("No se encontro al usuario con ese Id");
        }
        return customer;
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
        if (customer.getName().isEmpty() || customer.getName().length() < 3 || customer.getName().equals(" ") || customer.getName() == null) {
            throw new Exception("El nombre ingresado es invalido");
        }
        if (customer.getLastName().isEmpty() || customer.getLastName().equals(" ") || customer.getLastName() == null || customer.getLastName().length() < 3) {
            throw new Exception("El apellido ingreado es invalido");
        }
        if (customer.getEmail().isEmpty() || customer.getEmail() == null || customer.getEmail().equals(" ") || customer.getEmail().length() < 8) {
            throw new Exception("El email ingresado es invalido");
        }
        if (customer.getPassword().isEmpty() || customer.getPassword() == null || customer.getPassword().equals(" ") || (customer.getPassword().length() < 8)) {
            throw new Exception("La contraseña ingresada es invalida");
        }//si el dni ingresado es menor a 10 millones o mayor a 90 millones se tiene como valor erroneo
        if (customer.getDni() < 10000000 || customer.getDni() > 90000000 || customer.getDni() == null || customer.getDni().toString().isEmpty() || customer.getDni().toString().equals(" ")) {
            throw new Exception("El DNI ingreado es invalido");
        }

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

}
