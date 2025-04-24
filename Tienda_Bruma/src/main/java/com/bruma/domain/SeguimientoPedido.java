package com.bruma.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "seguimiento_pedido")
public class SeguimientoPedido implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento")
    private Integer idSeguimiento;
    
    @ManyToOne
    @JoinColumn(name = "id_factura")
    private Factura factura;
    
    private String estado;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_cambio")
    private Date fechaCambio;
    
    private String comentario;
    
    // Constructor por defecto
    public SeguimientoPedido() {
        this.fechaCambio = new Date(); // Por defecto, la fecha actual
    }
    
    // Constructor para crear un nuevo seguimiento
    public SeguimientoPedido(Factura factura, String estado, String comentario) {
        this.factura = factura;
        this.estado = estado;
        this.comentario = comentario;
        this.fechaCambio = new Date();
    }

    public Integer getIdSeguimiento() {
        return idSeguimiento;
    }

    public void setIdSeguimiento(Integer idSeguimiento) {
        this.idSeguimiento = idSeguimiento;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(Date fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    
    
}