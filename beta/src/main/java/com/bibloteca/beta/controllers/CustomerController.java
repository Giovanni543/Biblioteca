package com.bibloteca.beta.controllers;

import com.bibloteca.beta.entities.Customer;
import com.bibloteca.beta.entities.Photo;
import com.bibloteca.beta.repositories.PhotoRepository;
import com.bibloteca.beta.services.CustomerService;
import com.bibloteca.beta.services.PhotoService;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    //kkk
    private CustomerService customerService;
    private PhotoService photoService;//tendria que autowirearlo?

    @Autowired
    public CustomerController(CustomerService customerService, PhotoService photoService) {
        this.customerService = customerService;
        this.photoService = photoService;
    }

    /*@InitBinder //Evita que spring bindee automaticamente este atributo
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("picture");
    }*/
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String ListCustomers(ModelMap model) {
        List<Customer> customers = customerService.getAll();
        model.addAttribute("customers", customers);
        return "/customer/list-customers.html";
    }

    @GetMapping("/form")
    public String showForm(ModelMap model, @RequestParam(required = false) String id) {
        try {
            if (id == null) {
                model.addAttribute("customer", new Customer());
            } else {
                Customer customer = customerService.findById(id);
                model.addAttribute("customer", customer);
            }
            return "customer/form";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "customer/form";
        }
    }

    @PostMapping("/form")
    public String saveCustomer(@ModelAttribute Customer customer, @RequestParam("archivo") MultipartFile archivo, RedirectAttributes attr) {
        try {
            Photo photo = photoService.save(archivo);
            
            customer.setPhoto(photo);
            System.out.println("Se seteo la imagen a customer");
            customerService.saveNew(customer);
            
            return "index";
            
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
            System.out.println("Exception en controlador: " + e.getMessage());
            return "redirect:/customer/form";
        }
    }

    @GetMapping("/profile")
    public String showProfile(ModelMap model, HttpSession http) {
        try {
            //Customer customer = customerService.findById(id);
            Customer customer = (Customer) http.getAttribute("customersession");
            System.out.println("cc"+ customer.toString());

            //Customer customer = (Customer) http.getAttribute("customersession");
            //Customer customer = customerService.findById(id);
            //Customer customer = (Customer) session.getAttribute("customersession");
            //f7531118e87ce45d57836c4f997e3754b49979d6
            model.addAttribute("customer", customer);
            return "/customer/profile";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "index";
        }
    }

    @GetMapping("/edit-profile")
    public String editGet(ModelMap model, HttpSession http) {
        try {
            Customer customer = (Customer) http.getAttribute("customersession");
            model.addAttribute("customer", customer);
            System.out.println("customer get: " + customer.toString());
            System.out.println("1");
            return "/customer/edit-profile";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            System.out.println("2");
            return "/customer/profile";
        }
    }

    @PostMapping("edit-profile")
    public String editPost(@ModelAttribute Customer customer, RedirectAttributes attr, HttpSession http) {
        try {
            
            customerService.save(customer);
            http.setAttribute("customersession", customer);//actualiza la sessión asi me aparece el customer actualizado
            attr.addFlashAttribute("success", "Edit del Perfil EXITOSO ");
            System.out.println("3");
            return "redirect:/logout";
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
            System.out.println("4");
            return "/index";
        }
    }

    @GetMapping("/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable String id) throws Exception {

        Optional<Photo> photoOptional = photoService.findById(id);//preguntar por este procedimiento porque lo tengo que hacer optional y no photo

        if (photoOptional != null) {
            Photo photo = photoOptional.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(photo.getMime()));
            return new ResponseEntity<>(photo.getContent(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        /*Customer customer = customerService.findById(id);
        if (customer != null && customer.getPicture() != null) {
            byte[] imagen = customer.getPicture();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imagen, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }*/
        //return (customer != null && customer.getPicture() != null) ? customer.getPicture() : new byte[0];
    }

}
