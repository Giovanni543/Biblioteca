package com.bibloteca.beta.controllers;

import com.bibloteca.beta.entities.Book;
import com.bibloteca.beta.entities.Customer;
import com.bibloteca.beta.entities.Photo;
import com.bibloteca.beta.entities.Sale;
import com.bibloteca.beta.entities.CartItem;
import com.bibloteca.beta.repositories.PhotoRepository;
import com.bibloteca.beta.services.BookService;
import com.bibloteca.beta.services.CartItemService;
import com.bibloteca.beta.services.CustomerService;
import com.bibloteca.beta.services.PhotoService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.Spliterators.iterator;
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

    private CustomerService customerService;
    private PhotoService photoService;
    private BookService bookService;
    private CartItemService cartItemService;

    @Autowired
    public CustomerController(CustomerService customerService, PhotoService photoService, BookService bookService, CartItemService cartItemService) {
        this.customerService = customerService;
        this.photoService = photoService;
        this.bookService = bookService;
        this.cartItemService = cartItemService;
    }

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
    
    @PostMapping("/addBalance")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addBalance(HttpSession http, RedirectAttributes attr) throws Exception{
        try{
            Customer sessionCustomer = (Customer) http.getAttribute("customersession");
            Customer customer = customerService.findById(sessionCustomer.getId());
            
            customerService.addBalance(customer, 100000.0);
            
            attr.addFlashAttribute("Success", "Se agregaron $100.000 al saldo");
            
            return "redirect:/customer/profile";
        }catch(Exception e){
            System.out.println("ERROR");
            e.printStackTrace();
            attr.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/profile";
        }
    }

    @GetMapping("/profile")
    public String showProfile(ModelMap model, HttpSession http) {
        try {
            Customer sessionCustomer = (Customer) http.getAttribute("customersession");//Llamo objeto de session para traer objeto de bbdd
            Customer customer = customerService.findById(sessionCustomer.getId());//Hibernate vuelve a abrir la sesión y puede cargar todas las relaciones

            customer.getPurchaseHistory().sort(Comparator.comparing(Sale :: getSaleDate, Comparator.nullsLast(Comparator.reverseOrder())));//ordena purchaseHistory antes de enviarlo a la vista
            
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
            //customer.setPassword(null);//evito enviar y mostrar la contraseña a la vista (no se elimina ni sobreescribe)
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

    @PostMapping("/edit-profile")
    public String editPost(@ModelAttribute Customer customer, @RequestParam("photoFile") MultipartFile file, @RequestParam(value = "newPassword", required = false) String newPassword, RedirectAttributes attr, HttpSession http) {
        try {

            Customer actualizado = customerService.update(customer, file, newPassword);//La foto y Contraseña los gestiono separado de los demas atributos
            http.setAttribute("customersession", actualizado);//actualiza la sessión asi me aparece el customer actualizado
            attr.addFlashAttribute("success", "Edit del Perfil EXITOSO ");
            System.out.println("3");
            return "redirect:/logout";
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
            System.out.println("4, error: " + e.getMessage());
            return "/index";
        }
    }

    @GetMapping("/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable String id) throws Exception {

        Photo photo = photoService.findById(id);//preguntar por este procedimiento porque lo tengo que hacer optional y no photo

        if (photo != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(photo.getMime()));
            return new ResponseEntity<>(photo.getContent(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/buyBook")//tengo que ver como añado compras "sale" al purchased history
    public String buyGet(@RequestParam String id, ModelMap model, HttpSession http) {
        try {
            Customer sessionCustomer = (Customer) http.getAttribute("customersession");
            Customer customer = customerService.findById(sessionCustomer.getId());
            
            Book book = bookService.findById(id);
            
            CartItem item = new CartItem();
            
            item.setBook(book);
            item.setCustomer(customer);
            item.setQuantity(1);
            
            List<CartItem> items = new ArrayList<>();//lista temporal para comprar solo un libro, no toca BBDD
            items.add(item);
            
            model.addAttribute("customer", customer);
            model.addAttribute("cart", items);
            model.addAttribute("totalAmount", book.getPrice());
            model.addAttribute("buyNow", true);
            System.out.println(book.toString());
            
            return "customer/checkout";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "redirect:/book/vieww?id="+ id;
        }
    }

    @PostMapping("/buyBook")
    public String buyPost(@RequestParam String id, @RequestParam Integer quantity, RedirectAttributes attr, HttpSession http) {
        try {
            Customer sessionCustomer = (Customer) http.getAttribute("customersession");
            Customer customer = customerService.findById(sessionCustomer.getId());
            
            System.out.println("ENTRO AL POST ");
            
            customerService.buyBook(customer, id, quantity);
            
            attr.addFlashAttribute("success", "Compra realizada correctamente");
            System.out.println("VOLVIO DEL SERVICIO, DE NUEVO EN EL POST");

            return "redirect:/customer/profile";
        } catch (Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
            attr.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/cart";
        }
    }

    @GetMapping("/cart")
    public String viewCart(ModelMap model, HttpSession http) throws Exception {
        
        Customer sessionCustomer = (Customer) http.getAttribute("customersession");
        System.out.println("###  SESSION CUSTOMER : "+ sessionCustomer);
        
        Customer customer = customerService.findById(sessionCustomer.getId());
        System.out.println("---###--- CUSTOMER BD : "+ customer);
        
        List<CartItem> cart = cartItemService.getCart(customer);
        System.out.println("___### CART : "+ cart);
        
        Double total = cartItemService.getTotal(customer);

        model.addAttribute("customer", customer);//seteo los atributos al modelo
        model.addAttribute("cart", cart);
        model.addAttribute("totalAmount", total);
        model.addAttribute("buyNow", false);
        return "customer/checkout";//La vista para ver el carrito y la del checkout son las mismas (checkout)
    }

    @PostMapping("/cart/add")//El Carrito va a estar guardado en la sesion del usuario(HttpSession atributo llamado cart, que contiene List<Book>)
    public String addToCart(@RequestParam String id, HttpSession http, RedirectAttributes attr) {
        try {
            Customer sessionCustomer = (Customer) http.getAttribute("customersession");
            Customer customer = customerService.findById(sessionCustomer.getId());
            
            cartItemService.addBook(customer, id);
            
            attr.addFlashAttribute("success", "Libro agregado al carrito");
            System.out.println("Libro agregado al carrito");

            return "redirect:/customer/cart";
        } catch (Exception e) {
            System.out.println("No hay suficiente stock");
            attr.addFlashAttribute("error", e.getMessage());
            return "redirect:/book";
        }
    }

    @PostMapping("/cart/remove")
    public String removeBook(@RequestParam String id, HttpSession http, RedirectAttributes attr) {
        try {
            Customer sessionCustomer = (Customer) http.getAttribute("customersession");
            Customer customer = customerService.findById(sessionCustomer.getId());
            
            cartItemService.removeBook(customer, id);
            System.out.println("---###--- REMOVE OK");
            
            return "redirect:/customer/cart";
        } catch (Exception e) {
            System.out.println("!!!!!!!! ERROR EN REMOVE !!!!!!!!");
            e.printStackTrace();
            attr.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/cart";
        }
    }

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession http, RedirectAttributes attr) throws Exception {

        try{
            Customer sessionCustomer = (Customer) http.getAttribute("customersession");
            Customer customer = customerService.findById(sessionCustomer.getId());

            cartItemService.clearCart(customer);
            attr.addFlashAttribute("success", "Carrito vaciado correctamente");
            
            return "redirect:/customer/cart";
        }catch(Exception e){
            attr.addFlashAttribute("Error", e.getMessage());
            return "redirect:/customer/cart";
        }
    }
    
    @PostMapping("/cart/checkout")
    public String checkoutCart(HttpSession http, RedirectAttributes attr) throws Exception{
        
        System.out.println("ENTRO AL CHECKOUT DE CARRITO");
        Customer sessionCustomer = (Customer) http.getAttribute("customersession");
        Customer customer = customerService.findById(sessionCustomer.getId());
        
        List<CartItem> items = cartItemService.getCart(customer);
        
        customerService.buyBooks(customer, items);
        
        cartItemService.clearCart(customer);
        
        attr.addFlashAttribute("success", "Compra realizada");
        
        return "redirect:/customer/profile";
    }

    //@GetMapping("/purchase-history")
}
