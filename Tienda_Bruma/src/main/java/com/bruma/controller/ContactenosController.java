package com.bruma.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactenosController {
    
    @GetMapping("/contactenos")
    public String contactenos() {
        // Simplemente retorna el nombre de la vista (contactenos.html)
        return "contactenos/contactenos";
    }
}
