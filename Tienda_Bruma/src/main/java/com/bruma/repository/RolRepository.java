package com.bruma.repository;

import com.bruma.domain.Rol;
import com.bruma.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    // Método para encontrar roles por usuario
    List<Rol> findByUsuario(Usuario usuario);
    
    // Método para encontrar roles por ID de usuario
    @Query("SELECT r FROM Rol r WHERE r.usuario.idUsuario = :idUsuario")
    List<Rol> findByUsuarioId(@Param("idUsuario") Integer idUsuario);
    
    // Método para eliminar roles por ID de usuario
    @Modifying
    @Transactional
    @Query("DELETE FROM Rol r WHERE r.usuario.idUsuario = :idUsuario")
    void deleteByUsuarioId(@Param("idUsuario") Integer idUsuario);
}