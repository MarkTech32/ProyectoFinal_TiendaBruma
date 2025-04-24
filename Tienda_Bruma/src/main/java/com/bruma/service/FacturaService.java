package com.bruma.service;

import com.bruma.domain.Direccion;
import com.bruma.domain.Factura;
import com.bruma.domain.MetodoPago;
import com.bruma.domain.SeguimientoPedido;
import com.bruma.domain.Usuario;
import com.bruma.domain.Venta;
import com.bruma.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;
    
    @Autowired
    private SeguimientoPedidoService seguimientoPedidoService;
    
    @Transactional(readOnly = true)
    public List<Factura> encontrarPorUsuario(Integer idUsuario) {
        return facturaRepository.findByUsuarioIdUsuarioOrderByFechaDesc(idUsuario);
    }
    
    // Busca una factura por su ID
    @Transactional(readOnly = true)
    public Factura encontrarPorId(Integer idFactura) {
        return facturaRepository.findById(idFactura).orElse(null);
    }
    
    // Busca facturas por estado
    @Transactional(readOnly = true)
    public List<Factura> encontrarPorEstado(Integer estado) {
        return facturaRepository.findByEstadoOrderByFechaDesc(estado);
    }
    
    // Busca facturas por usuario y estado
    @Transactional(readOnly = true)
    public List<Factura> encontrarPorUsuarioYEstado(Integer idUsuario, Integer estado) {
        return facturaRepository.findByUsuarioIdUsuarioAndEstadoOrderByFechaDesc(idUsuario, estado);
    }
    
    // Crea una nueva factura
    @Transactional
    public Factura crearFactura(Usuario usuario, Direccion direccion, MetodoPago metodoPago, List<Venta> items, Double total) {
        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setDireccion(direccion);
        factura.setMetodoPago(metodoPago);
        factura.setFecha(new Date());
        factura.setTotal(total);
        factura.setEstado(1); // En Proceso
        
        // Calcular fecha estimada de entrega (5 días después)
        Date fechaEstimada = new Date();
        fechaEstimada.setTime(fechaEstimada.getTime() + 5 * 24 * 60 * 60 * 1000);
        factura.setFechaEntregaEstimada(fechaEstimada);
        
        // Guardar la factura
        Factura facturaGuardada = facturaRepository.save(factura);
        
        // Asignar la factura a cada item de venta
        for (Venta item : items) {
            item.setFactura(facturaGuardada);
        }
        
        // Registrar el primer estado en el seguimiento
        seguimientoPedidoService.registrarCambioEstado(facturaGuardada, "En Proceso", "Pedido recibido y en procesamiento");
        
        return facturaGuardada;
    }
    
    // Actualiza el estado de una factura
    @Transactional
    public void actualizarEstado(Factura factura, Integer nuevoEstado, String comentario) {
        factura.setEstado(nuevoEstado);
        
        // Actualizar fechas según el estado
        Date ahora = new Date();
        switch (nuevoEstado) {
            case 2: // Confirmado
                factura.setFechaConfirmacion(ahora);
                break;
            case 4: // En Camino
                factura.setFechaEnvio(ahora);
                break;
            case 5: // Entregado
                factura.setFechaEntregaReal(ahora);
                break;
        }
        
        facturaRepository.save(factura);
        
        // Registrar el cambio en el seguimiento
        String estadoTexto = factura.getEstadoTexto();
        seguimientoPedidoService.registrarCambioEstado(factura, estadoTexto, comentario);
    }
    
    // Cancelar una factura
    @Transactional
    public boolean cancelarFactura(Factura factura, String motivo) {
        // Solo se puede cancelar si está en proceso, confirmado o en preparación
        if (factura.getEstado() >= 4) {
            return false;
        }
        
        factura.setEstado(6); // Anulado
        facturaRepository.save(factura);
        
        // Registrar la cancelación en el seguimiento
        seguimientoPedidoService.registrarCambioEstado(factura, "Anulado", motivo);
        
        return true;
    }
    
    // Guardar o actualizar una factura
    @Transactional
    public void guardar(Factura factura) {
        facturaRepository.save(factura);
    }
    
    @Transactional(readOnly = true)
    public List<Factura> encontrarTodas() {
        return facturaRepository.findAll();
    }

    // Busca facturas por nombre, apellido o email de usuario (parcial)
    @Transactional(readOnly = true)
    public List<Factura> encontrarPorNombreUsuario(String nombreUsuario) {
        return facturaRepository.findByUsuarioNombreContainingOrUsuarioApellidosContainingOrUsuarioUsernameContainingOrderByFechaDesc(
                nombreUsuario, nombreUsuario, nombreUsuario);
    }

    // Busca facturas desde una fecha específica
    @Transactional(readOnly = true)
    public List<Factura> encontrarDesdeFecha(Date fecha) {
        return facturaRepository.findByFechaGreaterThanEqualOrderByFechaDesc(fecha);
    }
}