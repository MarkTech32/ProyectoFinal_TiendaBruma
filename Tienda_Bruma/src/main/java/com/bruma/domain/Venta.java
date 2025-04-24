package com.bruma.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

//@Data
@Entity
@Table(name = "venta")
public class Venta implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;
    
    @ManyToOne
    @JoinColumn(name = "id_factura")
    private Factura factura;
    
    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;
    
    private Double precio;
    private Integer cantidad;
    
    // Método para calcular el subtotal
    public Double getSubtotal() {
        return precio * cantidad;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    
}