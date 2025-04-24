package com.bruma.service;

import com.bruma.domain.Carrito;
import com.bruma.domain.CarritoItem;
import com.bruma.domain.Factura;
import com.bruma.domain.Producto;
import com.bruma.domain.Venta;
import com.bruma.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private ProductoService productoService;
    
    // Busca todos los ítems de una factura
    @Transactional(readOnly = true)
    public List<Venta> encontrarPorFactura(Integer idFactura) {
        return ventaRepository.findByFacturaIdFactura(idFactura);
    }
    
    // Busca un ítem por su ID
    @Transactional(readOnly = true)
    public Venta encontrarPorId(Integer idVenta) {
        return ventaRepository.findById(idVenta).orElse(null);
    }
    
    // Crea un nuevo ítem de venta
    @Transactional
    public Venta crearVenta(Factura factura, Producto producto, Integer cantidad) {
        Venta venta = new Venta();
        venta.setFactura(factura);
        venta.setProducto(producto);
        venta.setPrecio(producto.getPrecio());
        venta.setCantidad(cantidad);
        
        return ventaRepository.save(venta);
    }
    
    // Crea múltiples ítems de venta a partir de un carrito
    @Transactional
    public List<Venta> crearVentasDesdeCarrito(Factura factura, Carrito carrito) {
        List<Venta> ventas = new ArrayList<>();
        
        for (CarritoItem item : carrito.getItems()) {
            Producto producto = item.getProducto();
            int cantidad = item.getCantidad();
            
            if (producto != null && producto.getExistencias() >= cantidad) {
                Venta venta = crearVenta(factura, producto, cantidad);
                ventas.add(venta);
                
                // Actualizar existencias del producto
                producto.setExistencias(producto.getExistencias() - cantidad);
                productoService.save(producto);
            }
        }
        
        return ventas;
    }
    
    // Guardar o actualizar un ítem de venta
    @Transactional
    public void guardar(Venta venta) {
        ventaRepository.save(venta);
    }
}