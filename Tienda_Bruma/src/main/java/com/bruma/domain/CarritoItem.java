package com.bruma.domain;

import lombok.Data;

@Data
public class CarritoItem {
    
    private Producto producto;
    private int cantidad;
    private double subtotal;
    
    public CarritoItem(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        calcularSubtotal();
    }
    
    public void calcularSubtotal() {
        this.subtotal = this.producto.getPrecio() * this.cantidad;
    }
    
    public void incrementarCantidad() {
        this.cantidad++;
        calcularSubtotal();
    }
    
    public void decrementarCantidad() {
        if (this.cantidad > 1) {
            this.cantidad--;
            calcularSubtotal();
        }

    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    
    
}
