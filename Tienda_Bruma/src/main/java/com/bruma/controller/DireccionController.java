package com.bruma.controller;

import com.bruma.domain.Direccion;
import com.bruma.domain.Usuario;
import com.bruma.service.DireccionService;
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
@RequestMapping("/direcciones")
public class DireccionController {
 
    @Autowired
    private DireccionService direccionService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    
    //Muestra el listado de direcciones del usuario autenticado
    
    @GetMapping("")
    public String listado(Model model) {
        // Obtener el usuario actual de la sesión
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Obtener direcciones del usuario
        List<Direccion> direcciones = direccionService.encontrarPorUsuario(usuario.getIdUsuario());
        model.addAttribute("direcciones", direcciones);
        
        return "direcciones/listado";
    }
    
  
    //Muestra el formulario para agregar una nueva dirección
    
    @GetMapping("/nueva")
    public String nueva(Model model) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        Direccion direccion = new Direccion();
        direccion.setUsuario(usuario);
        direccion.setActivo(true);
        model.addAttribute("direccion", direccion);
        
        return "direcciones/form";
    }
    
    
    //Muestra el formulario para editar una dirección existente
    
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Buscar la dirección
        Direccion direccion = direccionService.encontrarPorId(id);
        
        // Verificar que la dirección exista y pertenezca al usuario
        if (direccion == null || !direccion.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            redirectAttributes.addFlashAttribute("error", "Dirección no encontrada o no autorizada");
            return "redirect:/direcciones";
        }
        
        model.addAttribute("direccion", direccion);
        return "direcciones/form";
    }
    
    
    //Procesa el formulario para guardar una dirección (nueva o editada)
    
    @PostMapping("/guardar")
    public String guardar(Direccion direccion, RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }

        // Asignar el usuario a la dirección
        direccion.setUsuario(usuario);

        // Si se marca como principal, desmarcar las demás
        if (direccion.isEsPrincipal()) {
            direccionService.quitarPredeterminada(usuario.getIdUsuario());

            // MODIFICACIÓN: Actualizar también la dirección en el perfil del usuario
            usuario.setDireccion(direccion.getDireccionCompleta());
            usuarioService.guardar(usuario);
        }

        // Guardar la dirección
        direccionService.guardar(direccion);

        String mensaje = (direccion.getIdDireccion() == null)
                ? "Dirección agregada con éxito"
                : "Dirección actualizada con éxito";
        redirectAttributes.addFlashAttribute("mensaje", mensaje);

        return "redirect:/direcciones";
    }
    
    
    //Establece una dirección como predeterminada
    
    @GetMapping("/predeterminada/{id}")
    public String establecerPredeterminada(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }

        // Verificar que la dirección exista y pertenezca al usuario
        Direccion direccion = direccionService.encontrarPorId(id);
        if (direccion == null || !direccion.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            redirectAttributes.addFlashAttribute("error", "Dirección no encontrada o no autorizada");
            return "redirect:/direcciones";
        }

        // Establecer como predeterminada
        direccionService.quitarPredeterminada(usuario.getIdUsuario());
        direccion.setEsPrincipal(true);
        direccionService.guardar(direccion);

        // MODIFICACIÓN: Actualizar también la dirección en el perfil del usuario
        usuario.setDireccion(direccion.getDireccionCompleta());
        usuarioService.guardar(usuario);

        redirectAttributes.addFlashAttribute("mensaje", "Dirección establecida como predeterminada");
        return "redirect:/direcciones";
    }
    
    
    //Elimina una dirección
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Verificar que la dirección exista y pertenezca al usuario
        Direccion direccion = direccionService.encontrarPorId(id);
        if (direccion == null || !direccion.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            redirectAttributes.addFlashAttribute("error", "Dirección no encontrada o no autorizada");
            return "redirect:/direcciones";
        }
        
        // Si es la dirección predeterminada y hay más direcciones, no permitir eliminarla
        if (direccion.isEsPrincipal()) {
            List<Direccion> direcciones = direccionService.encontrarPorUsuario(usuario.getIdUsuario());
            if (direcciones.size() > 1) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede eliminar la dirección predeterminada. Establezca otra como predeterminada primero.");
                return "redirect:/direcciones";
            }
        }
        
        // Eliminar la dirección
        direccionService.eliminar(direccion);
        redirectAttributes.addFlashAttribute("mensaje", "Dirección eliminada con éxito");
        
        return "redirect:/direcciones";
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
