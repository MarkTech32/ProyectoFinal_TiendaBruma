package com.bruma.repository;

import com.bruma.domain.SeguimientoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeguimientoPedidoRepository extends JpaRepository<SeguimientoPedido, Integer> {
    
    // Buscar todo el historial de seguimiento de un pedido ordenado por fecha descendente
    List<SeguimientoPedido> findByFacturaIdFacturaOrderByFechaCambioDesc(Integer idFactura);
    
    // Buscar el último estado de un pedido
    SeguimientoPedido findFirstByFacturaIdFacturaOrderByFechaCambioDesc(Integer idFactura);
}