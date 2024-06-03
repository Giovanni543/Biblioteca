
package com.bibloteca.beta.controllers;

import com.bibloteca.beta.entities.Customer;
import com.bibloteca.beta.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class MainController {


    @GetMapping
    public String index() {
        return "index.html";
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error, ModelMap model) {
        if(error != null){
            model.put("error", "El Email o contraseña fueron ingresados incorrectamente");
        }
        return "login";
    }
    
    @GetMapping("/signUp")
    public String registrationForm(ModelMap model){//solo para customer?? y para autor??(como pasa en el securityConfig)
        model.addAttribute("customer", new Customer());
        return "customer/form";
    }
    
    @GetMapping("/registerRole")
    public String registrationRole(ModelMap model){
        return "registerRole";
    }
}
