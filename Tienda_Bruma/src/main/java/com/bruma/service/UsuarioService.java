package com.bruma.service;

import com.bruma.domain.Rol;
import com.bruma.domain.Usuario;
import com.bruma.repository.RolRepository;
import com.bruma.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
public class UsuarioService implements UserDetailsService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar el usuario por el username en la base de datos
        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario == null) {
            throw new UsernameNotFoundException(username);
        }

        // Si se encontró el usuario, extraer los roles
        List<GrantedAuthority> roles = new ArrayList<>();

        // Utilizar un bucle for-each con casting explícito o verificación de tipo
        for (Object rolObj : usuario.getRoles()) {
            if (rolObj instanceof Rol) {
                Rol rol = (Rol) rolObj;
                roles.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
            }
        }

        // Crear el objeto UserDetails que Spring Security va a utilizar
        return new User(usuario.getUsername(),
                usuario.getPassword(),
                usuario.isActivo(),
                true, true, true,
                roles);
    }
    
    // Métodos adicionales para el CRUD de usuarios
    
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
    
    @Transactional
    public void guardar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
    
    @Transactional(readOnly = true)
    public Usuario encontrarUsuario(Usuario usuario) {
        return usuarioRepository.findById(usuario.getIdUsuario()).orElse(null);
    }
    
    @Transactional
    public void eliminar(Usuario usuario) {
        rolRepository.deleteByUsuarioId(usuario.getIdUsuario());
        usuarioRepository.delete(usuario);
    }
    
    @Transactional(readOnly = true)
    public Usuario encontrarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    
}
