package com.bruma.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "metodo_pago")
public class MetodoPago implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    private String tipo;
    
    @Column(name = "nombre_titular")
    private String nombreTitular;
    
    @Column(name = "numero_tarjeta")
    private String numeroTarjeta;
    
    @Column(name = "mes_expiracion")
    private Integer mesExpiracion;
    
    @Column(name = "anio_expiracion")
    private Integer anioExpiracion;
    
    @Column(name = "es_principal")
    private boolean esPrincipal;
    
    private boolean activo;
    
    // Métodos getter y setter
    public Integer getIdMetodoPago() {
        return idMetodoPago;
    }

    public void setIdMetodoPago(Integer idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public Integer getMesExpiracion() {
        return mesExpiracion;
    }

    public void setMesExpiracion(Integer mesExpiracion) {
        this.mesExpiracion = mesExpiracion;
    }

    public Integer getAnioExpiracion() {
        return anioExpiracion;
    }

    public void setAnioExpiracion(Integer anioExpiracion) {
        this.anioExpiracion = anioExpiracion;
    }

    public boolean isEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    // Método para obtener la tarjeta "oculta" (mostrar solo los últimos 4 dígitos)
    public String getNumeroTarjetaEnmascarado() {
        if (numeroTarjeta == null || numeroTarjeta.length() < 4) {
            return "";
        }
        String ultimos4Digitos = numeroTarjeta.substring(numeroTarjeta.length() - 4);
        return "**** **** **** " + ultimos4Digitos;
    }
    
    // Método para obtener fecha de expiración formateada
    public String getExpiracionFormateada() {
        return (mesExpiracion < 10 ? "0" : "") + mesExpiracion + "/" + anioExpiracion;
    }
}
