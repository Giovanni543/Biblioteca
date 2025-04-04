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
    //kkk
    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

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
    public String showForm(ModelMap model, @RequestParam(required = false) String id){
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
    public String saveCustomer(@ModelAttribute Customer customer, ModelMap model, RedirectAttributes attr){
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
    public String showProfile(ModelMap model, HttpSession http){
        try {
<<<<<<< HEAD
            //Customer customer = customerService.findById(id);
            Customer customer = (Customer) http.getAttribute("customersession");
            System.out.println("cc"+ customer.toString());
=======
            Customer customer = (Customer) http.getAttribute("customersession");
            //Customer customer = customerService.findById(id);
            //Customer customer = (Customer) session.getAttribute("customersession");
>>>>>>> f7531118e87ce45d57836c4f997e3754b49979d6
            model.addAttribute("customer", customer);
            return "/customer/profile";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "index";
        }
    }

    @GetMapping("/edit-profile")
    public String editGet(ModelMap model, HttpSession http){
        try {
            
            Customer customer = (Customer) http.getAttribute("customersession");
            model.addAttribute("customer", customer);
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
    public String editPost(Customer customer, ModelMap model, RedirectAttributes attr){//no funciona, tengo que traer el id y no consigo traerla a esta función
        try {
            System.out.println("customer post: "+customer.toString());
            //customer = customerService.findById(id);
            customer = customerService.findyEmail(customer.getEmail());
            System.out.println("customer post2 : "+customer.toString());
            //customer = (Customer) http.getAttribute("customersession");
            //customer = customerService.findById(id);
            
            customerService.save(customer);
            System.out.println("3");
            return "redirect:/customer/profile";
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
            System.out.println("4");
            return "/index";
        }
    }

}
