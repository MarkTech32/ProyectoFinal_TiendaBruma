package com.bruma.repository;

import com.bruma.domain.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {
    
    // Buscar todos los métodos de pago activos de un usuario
    List<MetodoPago> findByUsuarioIdUsuarioAndActivoOrderByEsPrincipalDesc(Integer idUsuario, boolean activo);
    
    // Buscar método de pago principal de un usuario
    MetodoPago findByUsuarioIdUsuarioAndEsPrincipalAndActivo(Integer idUsuario, boolean esPrincipal, boolean activo);
    
    // Establecer todos los métodos de pago de un usuario como no principales
    @Modifying
    @Transactional
    @Query("UPDATE MetodoPago mp SET mp.esPrincipal = false WHERE mp.usuario.idUsuario = :idUsuario")
    void removeDefaultPaymentMethodForUser(@Param("idUsuario") Integer idUsuario);
}