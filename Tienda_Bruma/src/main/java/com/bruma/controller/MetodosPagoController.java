package com.bruma.controller;

import com.bruma.domain.MetodoPago;
import com.bruma.domain.Usuario;
import com.bruma.service.MetodoPagoService;
import com.bruma.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/metodos-pago")
public class MetodosPagoController {
    
    @Autowired
    private MetodoPagoService metodoPagoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    
    //Muestra el listado de métodos de pago del usuario autenticado
    
    @GetMapping("")
    public String listado(Model model) {
        // Obtener el usuario actual de la sesión
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Obtener métodos de pago del usuario
        List<MetodoPago> metodosPago = metodoPagoService.encontrarPorUsuario(usuario.getIdUsuario());
        model.addAttribute("metodosPago", metodosPago);
        
        return "metodos-pago/listado";
    }
    
    
    //Muestra el formulario para agregar un nuevo método de pago
    
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setUsuario(usuario);
        metodoPago.setActivo(true);
        model.addAttribute("metodoPago", metodoPago);
        
        return "metodos-pago/form";
    }
    
    
    //Muestra el formulario para editar un método de pago existente
    
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Buscar el método de pago
        MetodoPago metodoPago = metodoPagoService.encontrarPorId(id);
        
        // Verificar que el método de pago exista y pertenezca al usuario
        if (metodoPago == null || !metodoPago.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            redirectAttributes.addFlashAttribute("error", "Método de pago no encontrado o no autorizado");
            return "redirect:/metodos-pago";
        }
        
        model.addAttribute("metodoPago", metodoPago);
        return "metodos-pago/form";
    }
    
    
    //Procesa el formulario para guardar un método de pago (nuevo o editado)
    
    @PostMapping("/guardar")
    public String guardar(MetodoPago metodoPago, RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Asignar el usuario al método de pago
        metodoPago.setUsuario(usuario);
        
        // Si se marca como principal, desmarcar los demás
        if (metodoPago.isEsPrincipal()) {
            metodoPagoService.quitarPredeterminado(usuario.getIdUsuario());
        }
        
        // Guardar el método de pago
        metodoPagoService.guardar(metodoPago);
        
        String mensaje = (metodoPago.getIdMetodoPago() == null) 
                ? "Método de pago agregado con éxito" 
                : "Método de pago actualizado con éxito";
        redirectAttributes.addFlashAttribute("mensaje", mensaje);
        
        return "redirect:/metodos-pago";
    }
    
    
    //Establece un método de pago como predeterminado
     
    @GetMapping("/predeterminado/{id}")
    public String establecerPredeterminado(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Verificar que el método de pago exista y pertenezca al usuario
        MetodoPago metodoPago = metodoPagoService.encontrarPorId(id);
        if (metodoPago == null || !metodoPago.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            redirectAttributes.addFlashAttribute("error", "Método de pago no encontrado o no autorizado");
            return "redirect:/metodos-pago";
        }
        
        // Establecer como predeterminado
        metodoPagoService.quitarPredeterminado(usuario.getIdUsuario());
        metodoPago.setEsPrincipal(true);
        metodoPagoService.guardar(metodoPago);
        
        redirectAttributes.addFlashAttribute("mensaje", "Método de pago establecido como predeterminado");
        return "redirect:/metodos-pago";
    }
    
    
    //Elimina un método de pago
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Verificar que el método de pago exista y pertenezca al usuario
        MetodoPago metodoPago = metodoPagoService.encontrarPorId(id);
        if (metodoPago == null || !metodoPago.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            redirectAttributes.addFlashAttribute("error", "Método de pago no encontrado o no autorizado");
            return "redirect:/metodos-pago";
        }
        
        // Si es el método de pago predeterminado y hay más métodos, no permitir eliminarlo
        if (metodoPago.isEsPrincipal()) {
            List<MetodoPago> metodosPago = metodoPagoService.encontrarPorUsuario(usuario.getIdUsuario());
            if (metodosPago.size() > 1) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede eliminar el método de pago predeterminado. Establezca otro como predeterminado primero.");
                return "redirect:/metodos-pago";
            }
        }
        
        // Eliminar el método de pago
        metodoPagoService.eliminar(metodoPago);
        redirectAttributes.addFlashAttribute("mensaje", "Método de pago eliminado con éxito");
        
        return "redirect:/metodos-pago";
    }
    
    
    //Método auxiliar para obtener el usuario actualmente autenticado
    
    private Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        String username;
        
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        return usuarioService.encontrarPorUsername(username);
    }
}
