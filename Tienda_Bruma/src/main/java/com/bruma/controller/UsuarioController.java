package com.bruma.controller;
import com.bruma.domain.Rol;
import com.bruma.domain.Usuario;
import com.bruma.repository.RolRepository;
import com.bruma.service.FirebaseStorageService;
import com.bruma.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private FirebaseStorageService firebaseStorageService;
    
    @Autowired
    private RolRepository rolRepository;

    @GetMapping("/listado")
    public String listado(Model model) {
        var usuarios = usuarioService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/listado";
    }

    @GetMapping("/nuevo")
    public String usuarioNuevo(Usuario usuario) {
        return "usuarios/form";
    }

    @PostMapping("/guardar")
    public String guardar(Usuario usuario, 
                          @RequestParam(value = "nuevoRol", required = false) String nuevoRol, 
                          @RequestParam("imagenFile") MultipartFile imagenFile,
                          RedirectAttributes redirectAttributes) {
        
        // Verificar usuario existente
        Usuario usuarioExistente = null;
        
        if (usuario.getIdUsuario() != null) {
            // Si estamos editando, recuperar el usuario existente con sus roles
            usuarioExistente = usuarioService.encontrarUsuario(usuario);
            
            // Verificar si existe otro usuario con ese username
            Usuario otroUsuario = usuarioService.encontrarPorUsername(usuario.getUsername());
            if (otroUsuario != null && !otroUsuario.getIdUsuario().equals(usuario.getIdUsuario())) {
                redirectAttributes.addFlashAttribute("error", "Ya existe otro usuario con ese nombre de usuario");
                return "redirect:/usuario/editar/" + usuario.getIdUsuario();
            }
            
            // Preservar los roles existentes
            if (usuarioExistente != null) {
                // Importante: Cargar explícitamente los roles del usuario existente desde la base de datos
                List<Rol> rolesExistentes = rolRepository.findByUsuarioId(usuarioExistente.getIdUsuario());
                if (rolesExistentes != null && !rolesExistentes.isEmpty()) {
                    // Si tenemos roles existentes, los asignamos al usuario
                    usuario.setRoles(rolesExistentes);
                } else {
                    // Si no hay roles, inicializamos la lista
                    usuario.setRoles(new ArrayList<>());
                }
            }
        } else {
            // Verificar si ya existe un usuario con ese username
            Usuario existente = usuarioService.encontrarPorUsername(usuario.getUsername());
            if (existente != null) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un usuario con ese nombre de usuario");
                return "redirect:/usuario/nuevo";
            }
            
            // Si es un usuario nuevo, inicializar la lista de roles
            usuario.setRoles(new ArrayList<>());
        }

        // Procesar imagen
        if (!imagenFile.isEmpty()) {
            // Si es un usuario nuevo, guardarlo primero para obtener el ID
            if (usuario.getIdUsuario() == null) {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
                usuarioService.guardar(usuario);
            }
            
            String rutaImagen = firebaseStorageService.cargaImagen(
                    imagenFile, 
                    "usuarios",
                    usuario.getIdUsuario());
            
            usuario.setRutaImagen(rutaImagen);
        }

        // Encriptar contraseña si es necesario
        if (usuario.getIdUsuario() == null || 
            (usuario.getPassword() != null && !usuario.getPassword().isEmpty() && 
             !usuario.getPassword().startsWith("$2a$"))) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        } else if (usuarioExistente != null && (usuario.getPassword() == null || usuario.getPassword().isEmpty())) {
            // Mantener la contraseña actual si no se proporciona una nueva
            usuario.setPassword(usuarioExistente.getPassword());
        }
        
        // Añadir nuevo rol si se ha especificado
        if (nuevoRol != null && !nuevoRol.isEmpty()) {
            Rol rol = new Rol();
            rol.setNombre(nuevoRol);
            rol.setUsuario(usuario);
            if (usuario.getRoles() == null) {
                usuario.setRoles(new ArrayList<>());
            }
            usuario.getRoles().add(rol);
        }

        // Guardar el usuario y los roles (si cascade=ALL, se guardarán automáticamente)
        usuarioService.guardar(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario guardado con éxito");
        return "redirect:/usuario/listado";
    }

    @GetMapping("/editar/{idUsuario}")
    public String editar(Usuario usuario, Model model) {
        usuario = usuarioService.encontrarUsuario(usuario);
        // Asegurarse de que los roles estén cargados
        List<Rol> roles = rolRepository.findByUsuarioId(usuario.getIdUsuario());
        usuario.setRoles(roles);
        model.addAttribute("usuario", usuario);
        return "usuarios/form";  
    }

    @GetMapping("/eliminar/{idUsuario}")
    public String eliminar(Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminar(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado con éxito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/usuario/listado";
    }
}