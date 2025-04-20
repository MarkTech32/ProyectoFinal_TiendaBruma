package com.bruma.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Carrito {
    
     private List<CarritoItem> items;
    private double total;
    private int itemCount;
    
    public Carrito() {
        this.items = new ArrayList<>();
        this.total = 0.0;
        this.itemCount = 0;
    }
    
    public void agregarProducto(Producto producto, int cantidad) {
        // Verificar si el producto ya existe en el carrito
        for (CarritoItem item : items) {
            if (item.getProducto().getIdProducto().equals(producto.getIdProducto())) {
                item.setCantidad(item.getCantidad() + cantidad);
                item.calcularSubtotal();
                calcularTotal();
                return;
            }
        }
        
        // Si no existe, añadir un nuevo item
        CarritoItem nuevoItem = new CarritoItem(producto, cantidad);
        items.add(nuevoItem);
        calcularTotal();
    }
    
    public void eliminarProducto(Integer idProducto) {
        items.removeIf(item -> item.getProducto().getIdProducto().equals(idProducto));
        calcularTotal();
    }
    
    public void actualizarCantidad(Integer idProducto, int cantidad) {
        for (CarritoItem item : items) {
            if (item.getProducto().getIdProducto().equals(idProducto)) {
                item.setCantidad(cantidad);
                item.calcularSubtotal();
                calcularTotal();
                return;
            }
        }
    }
    
    public void incrementarCantidad(Integer idProducto) {
        for (CarritoItem item : items) {
            if (item.getProducto().getIdProducto().equals(idProducto)) {
                item.incrementarCantidad();
                calcularTotal();
                return;
            }
        }
    }
    
    public void decrementarCantidad(Integer idProducto) {
        for (CarritoItem item : items) {
            if (item.getProducto().getIdProducto().equals(idProducto)) {
                item.decrementarCantidad();
                calcularTotal();
                return;
            }
        }
    }
    
    public void calcularTotal() {
        this.total = 0;
        this.itemCount = 0;
        
        for (CarritoItem item : items) {
            this.total += item.getSubtotal();
            this.itemCount += item.getCantidad();
        }
    }
    
    public void vaciar() {
        items.clear();
        this.total = 0;
        this.itemCount = 0;
    }

    public List<CarritoItem> getItems() {
        return items;
    }

    public void setItems(List<CarritoItem> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
    
}
