package com.bibloteca.beta.services;

import com.bibloteca.beta.entities.Customer;
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
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Service
public class CustomerService implements UserDetailsService{

    private CustomerRepository customerRepository;

    @Autowired//la inyeccion de dependencia en los constructores nos permite hacer tessting despues de manera mas sencilla
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Exception.class)
    public void saveNew(Customer customer) throws Exception {
        
        validate(customer);
        activateIfNew(customer);
        System.out.println("paso la validacion y el activado");
        
        String passwordEncripted = new BCryptPasswordEncoder().encode(customer.getPassword());
        customer.setPassword(passwordEncripted);
        System.out.println("hasta aca todo bien");
        customerRepository.save(customer);
    }
    
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Exception.class)
    public void save(Customer customer)throws Exception{//si edito la contraseña de una cuenta, tendria que pasar de nuevo por el passwordEncoder?
        validate(customer);
        customerRepository.save(customer);
    }
    
    private void activateIfNew(Customer customer) throws Exception{
        if (customer.getActive() == null || customer.getActive().equals(false)) {
            customer.setActive(Boolean.TRUE);
            customer.setRole(Role.USER);
        }
    }

    @Transactional
    public Customer findById(String id) throws Exception{
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
    public Customer findyEmail(String email)throws Exception{
        Customer customer = customerRepository.findByEmail(email);
        if(customer == null){
            throw new Exception ("No se encontro a ningun usuario con dicho email");
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        
        Customer customer = customerRepository.findByEmail(email);
        if(customer == null){
            throw new UsernameNotFoundException("usuario no encontrado");
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
