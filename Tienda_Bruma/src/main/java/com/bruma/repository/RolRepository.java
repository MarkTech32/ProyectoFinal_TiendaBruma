package com.bruma.repository;

import com.bruma.domain.Rol;
import com.bruma.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RolRepository extends JpaRepository<Rol, Long> {
    // Método para encontrar roles por usuario
    List<Rol> findByUsuario(Usuario usuario);
    
    // Método para encontrar roles por ID de usuario
    @Query("SELECT r FROM Rol r WHERE r.usuario.idUsuario = :idUsuario")
    List<Rol> findByUsuarioId(@Param("idUsuario") Long idUsuario);
    
    // Método para eliminar roles por ID de usuario
    @Query("DELETE FROM Rol r WHERE r.usuario.idUsuario = :idUsuario")
    void deleteByUsuarioId(@Param("idUsuario") Long idUsuario);
}