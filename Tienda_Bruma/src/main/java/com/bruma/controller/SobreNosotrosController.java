package com.bruma.controller;

import com.bruma.domain.Review;
import com.bruma.service.ReviewService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SobreNosotrosController {
    
    @Autowired
    private ReviewService reviewService;
    
    @GetMapping("/sobre_nosotros")
    public String sobreNosotros(Model model) {
        // Obtenemos reseñas con calificación alta (4 o 5 estrellas) para el slider
        List<Review> reviewsDestacadas = reviewService.findByMinRating(4);
        model.addAttribute("reviews", reviewsDestacadas);
        
        return "sobre_nosotros/sobre_nosotros"; 
    }
    
}
