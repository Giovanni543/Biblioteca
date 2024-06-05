package com.bibloteca.beta.controllers;

import com.bibloteca.beta.entities.Customer;
import com.bibloteca.beta.services.CustomerService;
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
@RequestMapping("/customer")
public class CustomerController {

    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    //prueba para hacer commit y push

    //En la pag de customer va a aparecer una lista de todos los customers
    //una opcion para editar la cuenta con la que se ingreso
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String ListCustomers(ModelMap model) {
        List<Customer> customers = customerService.getAll();
        model.addAttribute("customers", customers);
        return "/customer/list-customers.html";
    }

    @GetMapping("/form")
    public String showForm(ModelMap model, @RequestParam(required = false) String id) throws Exception {
        try {
            if (id == null) {
                model.addAttribute("customer", new Customer());
                return "customer/form";
            } else {
                Customer customer = customerService.findById(id);
                model.addAttribute("customer", customer);
                return "customer/form";
            }
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "customer/form";
        }
    }

    @PostMapping("/form")
    public String saveCustomer(@ModelAttribute Customer customer, ModelMap model, RedirectAttributes attr) throws Exception {
        try {
            customerService.saveNew(customer);
            System.out.println("Customer ah sido guardado :)");
            return "redirect:/customer";
        } catch (Exception e) {
            //attr.addFlashAttribute("customer", customer); vuelve a recargar la pag con los atributos del customer pero no anda¿?
            attr.addFlashAttribute("error", e.getMessage());
            System.out.println("Exception en controlador: " + e.getMessage());
            return "redirect:/customer/form";
        }
    }

    @GetMapping("/profile")
    public String showProfile(ModelMap model, HttpSession session) throws Exception {
        try {
            Customer customer = (Customer) session.getAttribute("customersession");
            model.addAttribute("customer", customer);
            return "/customer/profile";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "index";
        }
    }

    @GetMapping("/edit-profile")
    public String editProfile(ModelMap model, HttpSession http) throws Exception {
        try {
            Customer customer = (Customer) http.getAttribute("customersession");
            model.addAttribute("customer", customer);
            model.addAttribute("id",  customer.getId());
            System.out.println("customer get: "+ customer.toString());
            System.out.println("1");
            return "/customer/edit-profile";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            System.out.println("2");
            return "/customer/profile";
        }
    }
    

    @PostMapping("edit-profile")
    public String editProfile(Customer customer, HttpSession http, RedirectAttributes attr) throws Exception {//no funciona, tengo que traer el id y no consigo traerla a esta función
        try {
            //System.out.println("id:  " +id);
            //System.out.println("cusomer:  " +customer.getId());
            //customer = customerService.findById(id);
            
            customer = (Customer) http.getAttribute("customersession");
            System.out.println(customer.toString());
            customerService.save(customer);
            System.out.println("customer post: "+customer.toString());
            System.out.println("3");
            return "redirect:/customer/profile";
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
            System.out.println("4");
            return "/index";
        }
    }

}
