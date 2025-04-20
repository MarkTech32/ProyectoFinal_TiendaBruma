package com.bruma.service;

import com.bruma.domain.Carrito;
import com.bruma.domain.Producto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarritoService {
    
    private final String CARRITO_SESSION_KEY = "carrito";
    
    @Autowired
    private ProductoService productoService;
    
    
    //Obtiene el carrito actual de la sesión o crea uno nuevo si no existe
    
    public Carrito getCarrito(HttpSession session) {
        Carrito carrito = (Carrito) session.getAttribute(CARRITO_SESSION_KEY);
        if (carrito == null) {
            carrito = new Carrito();
            session.setAttribute(CARRITO_SESSION_KEY, carrito);
        }
        return carrito;
    }
    
    
    //Agrega un producto al carrito
    
    public void agregarProducto(HttpSession session, Integer idProducto, int cantidad) {
        Carrito carrito = getCarrito(session);
        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        producto = productoService.getProducto(producto);
        
        // Verificar que la cantidad solicitada no exceda las existencias
        int cantidadActualEnCarrito = 0;
        for (var item : carrito.getItems()) {
            if (item.getProducto().getIdProducto().equals(idProducto)) {
                cantidadActualEnCarrito = item.getCantidad();
                break;
            }
        }
        
        int cantidadFinal = cantidadActualEnCarrito + cantidad;
        if (cantidadFinal > producto.getExistencias()) {
            cantidad = producto.getExistencias() - cantidadActualEnCarrito;
            if (cantidad <= 0) return; // No hay más existencias disponibles
        }
        
        carrito.agregarProducto(producto, cantidad);
        session.setAttribute(CARRITO_SESSION_KEY, carrito);
    }
    
    
    //Elimina un producto del carrito
    
    public void eliminarProducto(HttpSession session, Integer idProducto) {
        Carrito carrito = getCarrito(session);
        carrito.eliminarProducto(idProducto);
        session.setAttribute(CARRITO_SESSION_KEY, carrito);
    }
    
    
    //Actualiza la cantidad de un producto en el carrito
    
    public void actualizarCantidad(HttpSession session, Integer idProducto, int cantidad) {
        Carrito carrito = getCarrito(session);
        
        // Verificar que la cantidad no exceda las existencias
        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        producto = productoService.getProducto(producto);
        
        if (cantidad > producto.getExistencias()) {
            cantidad = producto.getExistencias();
        }
        
        if (cantidad <= 0) {
            carrito.eliminarProducto(idProducto);
        } else {
            carrito.actualizarCantidad(idProducto, cantidad);
        }
        
        session.setAttribute(CARRITO_SESSION_KEY, carrito);
    }
    
    
    //Incrementa la cantidad de un producto en el carrito
    
    public void incrementarCantidad(HttpSession session, Integer idProducto) {
        Carrito carrito = getCarrito(session);
        
        // Verificar que no exceda las existencias
        for (var item : carrito.getItems()) {
            if (item.getProducto().getIdProducto().equals(idProducto)) {
                if (item.getCantidad() < item.getProducto().getExistencias()) {
                    carrito.incrementarCantidad(idProducto);
                }
                break;
            }
        }
        
        session.setAttribute(CARRITO_SESSION_KEY, carrito);
    }
    
    
    //Decrementa/Quita items de la cantidad de un producto en el carrito
    
    public void decrementarCantidad(HttpSession session, Integer idProducto) {
        Carrito carrito = getCarrito(session);
        carrito.decrementarCantidad(idProducto);
        session.setAttribute(CARRITO_SESSION_KEY, carrito);
    }
    
    //Vacía el carrito
    
    public void vaciarCarrito(HttpSession session) {
        Carrito carrito = getCarrito(session);
        carrito.vaciar();
        session.setAttribute(CARRITO_SESSION_KEY, carrito);
    }
}
