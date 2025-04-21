package com.bruma.controller;

import com.bruma.domain.Review;
import com.bruma.domain.Usuario;
import com.bruma.service.ReviewService;
import com.bruma.service.UsuarioService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/listado")
    public String listado(Model model) {
        List<Review> reviews = reviewService.getActiveReviewsSorted();
        model.addAttribute("reviews", reviews);
        return "review/listado";
    }
    
    @GetMapping({"/nuevo", "/form"})
    public String reviewForm(Model model) {
        model.addAttribute("review", new Review());
        return "review/form";
    }
    
    @PostMapping("/guardar")
    public String guardar(Review review, RedirectAttributes redirectAttributes) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioService.encontrarPorUsername(username);
        
        // Asignar el usuario actual a la reseña
        review.setUsuario(usuario);
        
        // Establecer la fecha actual si es una nueva reseña
        if (review.getIdReview() == null) {
            review.setFechaCreacion(LocalDateTime.now());
        }
        
        // Guardar la reseña
        review.setActivo(true);
        reviewService.save(review);
        
        redirectAttributes.addFlashAttribute("msg", "Reseña guardada correctamente");
        return "redirect:/review/listado";
    }
    
    @GetMapping("/editar/{idReview}")
    public String editar(@PathVariable Long idReview, Model model) {
        Review review = reviewService.getReview(idReview);
        model.addAttribute("review", review);
        return "review/form";
    }
    
    @GetMapping("/eliminar/{idReview}")
    public String eliminar(@PathVariable Long idReview, RedirectAttributes redirectAttributes) {
        // En lugar de eliminar, marcamos como inactivo
        Review review = reviewService.getReview(idReview);
        if (review != null) {
            review.setActivo(false);
            reviewService.save(review);
            redirectAttributes.addFlashAttribute("msg", "Reseña eliminada correctamente");
        }
        return "redirect:/review/listado";
    }
    
    // Método para obtener las reviews para el slider en sobre_nosotros
    @GetMapping("/api/destacadas")
    @ResponseBody
    public List<Review> getReviewsDestacadas() {
        // Podríamos filtrar por rating alto, por ejemplo
        return reviewService.findByMinRating(4);
    }
}
