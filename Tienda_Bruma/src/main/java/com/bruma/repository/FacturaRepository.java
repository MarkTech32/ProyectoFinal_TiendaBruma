package com.bruma.repository;

import com.bruma.domain.Factura;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    
    // Buscar todas las facturas ordenadas por fecha descendente
    List<Factura> findByUsuarioIdUsuarioOrderByFechaDesc(Integer idUsuario);

    // Buscar facturas por estado
    List<Factura> findByEstadoOrderByFechaDesc(Integer estado);
    
    // Buscar facturas por usuario y estado
    List<Factura> findByUsuarioIdUsuarioAndEstadoOrderByFechaDesc(Integer idUsuario, Integer estado);
    
    // Buscar por nombre, apellido o email (username) de usuario
    List<Factura> findByUsuarioNombreContainingOrUsuarioApellidosContainingOrUsuarioUsernameContainingOrderByFechaDesc(
            String nombre, String apellido, String username);

// Buscar facturas desde una fecha específica
    List<Factura> findByFechaGreaterThanEqualOrderByFechaDesc(Date fecha);
}