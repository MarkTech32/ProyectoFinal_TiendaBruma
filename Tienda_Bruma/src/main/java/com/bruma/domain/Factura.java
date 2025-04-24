package com.bruma.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "factura")
public class Factura implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Integer idFactura;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "id_direccion")
    private Direccion direccion;
    
    @ManyToOne
    @JoinColumn(name = "id_metodo_pago")
    private MetodoPago metodoPago;
    
    @Temporal(TemporalType.DATE)
    private Date fecha;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_confirmacion")
    private Date fechaConfirmacion;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_envio")
    private Date fechaEnvio;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_entrega_estimada")
    private Date fechaEntregaEstimada;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_entrega_real")
    private Date fechaEntregaReal;
    
    private Double total;
    
    // 1=En Proceso, 2=Confirmado, 3=En Preparación, 4=En Camino, 5=Entregado, 6=Anulado
    private Integer estado;
    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Venta> items;
    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaCambio DESC")
    private List<SeguimientoPedido> seguimientos;
    
    // Método para obtener el estado como texto
    public String getEstadoTexto() {
        switch (estado) {
            case 1: return "En Proceso";
            case 2: return "Confirmado";
            case 3: return "En Preparación";
            case 4: return "En Camino";
            case 5: return "Entregado";
            case 6: return "Anulado";
            default: return "Desconocido";
        }
    }
    
    // Método para verificar si se puede cancelar el pedido
    public boolean sePuedeCancelar() {
        return estado < 4; // Solo se puede cancelar si está en proceso, confirmado o en preparación
    }
    
    // Método para verificar si está finalizado
    public boolean estaFinalizado() {
        return estado == 5; // Entregado
    }

    public Integer getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(Date fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Date getFechaEntregaEstimada() {
        return fechaEntregaEstimada;
    }

    public void setFechaEntregaEstimada(Date fechaEntregaEstimada) {
        this.fechaEntregaEstimada = fechaEntregaEstimada;
    }

    public Date getFechaEntregaReal() {
        return fechaEntregaReal;
    }

    public void setFechaEntregaReal(Date fechaEntregaReal) {
        this.fechaEntregaReal = fechaEntregaReal;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public List<Venta> getItems() {
        return items;
    }

    public void setItems(List<Venta> items) {
        this.items = items;
    }

    public List<SeguimientoPedido> getSeguimientos() {
        return seguimientos;
    }

    public void setSeguimientos(List<SeguimientoPedido> seguimientos) {
        this.seguimientos = seguimientos;
    }
    
    
}