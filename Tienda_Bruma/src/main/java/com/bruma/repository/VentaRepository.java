package com.bruma.repository;

import com.bruma.domain.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    
    // Buscar todos los ítems de una factura
    List<Venta> findByFacturaIdFactura(Integer idFactura);
}