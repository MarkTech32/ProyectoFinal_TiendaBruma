package com.bruma.service;

import com.bruma.domain.Factura;
import com.bruma.domain.SeguimientoPedido;
import com.bruma.repository.SeguimientoPedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SeguimientoPedidoService {

    @Autowired
    private SeguimientoPedidoRepository seguimientoPedidoRepository;
    
    // Busca todo el historial de seguimiento de un pedido ordenado por fecha descendente
    @Transactional(readOnly = true)
    public List<SeguimientoPedido> encontrarPorFactura(Integer idFactura) {
        return seguimientoPedidoRepository.findByFacturaIdFacturaOrderByFechaCambioDesc(idFactura);
    }
    
    // Busca el último estado de un pedido
    @Transactional(readOnly = true)
    public SeguimientoPedido encontrarUltimoEstado(Integer idFactura) {
        return seguimientoPedidoRepository.findFirstByFacturaIdFacturaOrderByFechaCambioDesc(idFactura);
    }
    
    // Registra un nuevo cambio de estado en el seguimiento
    @Transactional
    public SeguimientoPedido registrarCambioEstado(Factura factura, String estado, String comentario) {
        SeguimientoPedido seguimiento = new SeguimientoPedido();
        seguimiento.setFactura(factura);
        seguimiento.setEstado(estado);
        seguimiento.setComentario(comentario);
        seguimiento.setFechaCambio(new Date());
        
        return seguimientoPedidoRepository.save(seguimiento);
    }
}