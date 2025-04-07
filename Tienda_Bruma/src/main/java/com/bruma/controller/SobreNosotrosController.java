package com.bruma.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SobreNosotrosController {
    
    @GetMapping("/sobre_nosotros")
    public String sobreNosotros() {
        return "sobre_nosotros/sobre_nosotros"; // Quita la barra diagonal al principio
    }
    
}
