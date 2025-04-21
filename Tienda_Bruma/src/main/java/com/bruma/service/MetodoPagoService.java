package com.bruma.service;

import com.bruma.domain.MetodoPago;
import com.bruma.repository.MetodoPagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MetodoPagoService {

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;
    
    
    //Busca todos los métodos de pago activos de un usuario ordenados por predeterminado primero
    
    @Transactional(readOnly = true)
    public List<MetodoPago> encontrarPorUsuario(Integer idUsuario) {
        return metodoPagoRepository.findByUsuarioIdUsuarioAndActivoOrderByEsPrincipalDesc(idUsuario, true);
    }
    
    
    //Busca un método de pago por su ID
    
    @Transactional(readOnly = true)
    public MetodoPago encontrarPorId(Integer idMetodoPago) {
        return metodoPagoRepository.findById(idMetodoPago).orElse(null);
    }
    
    
    //Busca el método de pago principal de un usuario
    
    @Transactional(readOnly = true)
    public MetodoPago encontrarPrincipal(Integer idUsuario) {
        return metodoPagoRepository.findByUsuarioIdUsuarioAndEsPrincipalAndActivo(idUsuario, true, true);
    }
    
    
    //Elimina el marcado de método de pago predeterminado para todos los métodos de un usuario
    
    @Transactional
    public void quitarPredeterminado(Integer idUsuario) {
        metodoPagoRepository.removeDefaultPaymentMethodForUser(idUsuario);
    }
    
    
    //Guarda un método de pago
    
    @Transactional
    public void guardar(MetodoPago metodoPago) {
        metodoPagoRepository.save(metodoPago);
    }
    
   
    //Elimina un método de pago
    
    @Transactional
    public void eliminar(MetodoPago metodoPago) {
        metodoPago.setActivo(false);
        metodoPagoRepository.save(metodoPago);
    }
}
