package com.bruma.service;

import com.bruma.domain.Direccion;
import com.bruma.repository.DireccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DireccionService {

    @Autowired
    private DireccionRepository direccionRepository;
    
    
    //Busca todas las direcciones activas de un usuario ordenadas por predeterminada primero
    
    @Transactional(readOnly = true)
    public List<Direccion> encontrarPorUsuario(Integer idUsuario) {
        return direccionRepository.findByUsuarioIdUsuarioAndActivoOrderByEsPrincipalDesc(idUsuario, true);
    }
    
    
    //Busca una dirección por su ID
    
    @Transactional(readOnly = true)
    public Direccion encontrarPorId(Integer idDireccion) {
        return direccionRepository.findById(idDireccion).orElse(null);
    }
    
    
    //Busca la dirección principal de un usuario
    
    @Transactional(readOnly = true)
    public Direccion encontrarPrincipal(Integer idUsuario) {
        return direccionRepository.findByUsuarioIdUsuarioAndEsPrincipalAndActivo(idUsuario, true, true);
    }
    
    
    //Elimina el marcado de dirección predeterminada para todas las direcciones de un usuario
    
    @Transactional
    public void quitarPredeterminada(Integer idUsuario) {
        direccionRepository.removeDefaultAddressForUser(idUsuario);
    }
    
    
    //Guarda una dirección
    
    @Transactional
    public void guardar(Direccion direccion) {
        direccionRepository.save(direccion);
    }
    
    
    //Elimina una dirección (desactivándola, no eliminándola físicamente)
    
    @Transactional
    public void eliminar(Direccion direccion) {
        direccion.setActivo(false);
        direccionRepository.save(direccion);
    }
}