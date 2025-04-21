package com.bruma.controller;

import com.bruma.domain.Usuario;
import com.bruma.service.FirebaseStorageService;
import com.bruma.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class PerfilController {
    
@Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @GetMapping
    public String verPerfil(Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioService.encontrarPorUsername(username);
        if (usuario != null) {
            // NUEVO: Verificar si el usuario tiene dirección, y si no, intentar obtener la predeterminada
            if ((usuario.getDireccion() == null || usuario.getDireccion().isEmpty()) && usuario.getDireccionPrincipal() != null) {
                usuario.setDireccion(usuario.getDireccionPrincipal().getDireccionCompleta());
                usuarioService.guardar(usuario);
            }

            model.addAttribute("usuario", usuario);
            return "perfil/perfil_ver";
        } else {
            return "redirect:/";
        }
    }
    
    @GetMapping("/editar")
    public String editarPerfil(Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioService.encontrarPorUsername(username);
        if (usuario != null) {
            // NUEVO: Verificar si el usuario tiene dirección, y si no, intentar obtener la predeterminada
            if ((usuario.getDireccion() == null || usuario.getDireccion().isEmpty()) && usuario.getDireccionPrincipal() != null) {
                usuario.setDireccion(usuario.getDireccionPrincipal().getDireccionCompleta());
                usuarioService.guardar(usuario);
            }

            model.addAttribute("usuario", usuario);
            return "perfil/perfil_editar";
        } else {
            return "redirect:/";
        }
    }
    
    @PostMapping("/guardar")
    public String guardarPerfil(
            @ModelAttribute Usuario usuario,
            @RequestParam(value = "cambiarPassword", defaultValue = "false") boolean cambiarPassword,
            @RequestParam(value = "nuevaPassword", required = false) String nuevaPassword,
            @RequestParam(value = "confirmarPassword", required = false) String confirmarPassword,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {
        
        // Obtener el usuario original desde la base de datos
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Usuario usuarioOriginal = usuarioService.encontrarPorUsername(username);
        
        if (usuarioOriginal == null || !usuarioOriginal.getIdUsuario().equals(usuario.getIdUsuario())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar este perfil");
            return "redirect:/perfil";
        }
        
        // Actualizar solo los campos permitidos
        usuarioOriginal.setNombre(usuario.getNombre());
        usuarioOriginal.setApellidos(usuario.getApellidos());
        usuarioOriginal.setCorreo(usuario.getCorreo());
        usuarioOriginal.setTelefono(usuario.getTelefono());
        usuarioOriginal.setDireccion(usuario.getDireccion());
        
        // Actualizar contraseña si se solicitó
        if (cambiarPassword) {
            if (nuevaPassword != null && confirmarPassword != null && nuevaPassword.equals(confirmarPassword)) {
                usuarioOriginal.setPassword(passwordEncoder.encode(nuevaPassword));
            } else {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/perfil/editar";
            }
        }
        
        // Procesar imagen si se subió una nueva
        if (!imagenFile.isEmpty()) {
            String rutaImagen = firebaseStorageService.cargaImagen(
                    imagenFile,
                    "usuarios",
                    usuarioOriginal.getIdUsuario());
            
            usuarioOriginal.setRutaImagen(rutaImagen);
        }
        
        // Guardar cambios
        usuarioService.guardar(usuarioOriginal);
        redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado con éxito");
        return "redirect:/perfil";
    }
}