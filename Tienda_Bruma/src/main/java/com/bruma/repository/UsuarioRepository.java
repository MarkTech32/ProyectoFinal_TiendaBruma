package com.bruma.repository;

import com.bruma.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    Usuario findByUsername(String username);
}
