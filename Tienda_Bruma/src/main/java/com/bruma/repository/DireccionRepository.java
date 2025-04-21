package com.bruma.repository;

import com.bruma.domain.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Integer> {
    
    // Buscar todas las direcciones activas de un usuario
    List<Direccion> findByUsuarioIdUsuarioAndActivoOrderByEsPrincipalDesc(Integer idUsuario, boolean activo);
    
    // Buscar dirección principal de un usuario
    Direccion findByUsuarioIdUsuarioAndEsPrincipalAndActivo(Integer idUsuario, boolean esPrincipal, boolean activo);
    
    // Establecer todas las direcciones de un usuario como no principales
    @Modifying
    @Transactional
    @Query("UPDATE Direccion d SET d.esPrincipal = false WHERE d.usuario.idUsuario = :idUsuario")
    void removeDefaultAddressForUser(@Param("idUsuario") Integer idUsuario);
    
}
