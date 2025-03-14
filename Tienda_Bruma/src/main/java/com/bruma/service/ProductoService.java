package com.bruma.service;

import com.bruma.domain.Producto;
import com.bruma.repository.ProductoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Transactional(readOnly=true)
    public List<Producto> getProductos(boolean activos) {
        var productos = productoRepository.findAll();
        if (activos) {
            productos.removeIf(p -> !p.isActivo());
        }
        
        return productos;
    }
    
    @Transactional(readOnly=true)
    public List<Producto> getProductosPorCategoria(Long idCategoria) {
        var productos = productoRepository.findAll();
        return productos.stream()
                .filter(p -> p.getCategoria() != null && 
                             p.getCategoria().getIdCategoria() != null && 
                             p.getCategoria().getIdCategoria().equals(idCategoria))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly=true)
    public Producto getProducto(Producto producto) {
        return productoRepository.findById(producto.getIdProducto()).orElse(null);
    }
    
    @Transactional
    public void delete(Producto producto) {
        productoRepository.delete(producto);
    }
    
    @Transactional
    public void save(Producto producto) {
        productoRepository.save(producto);
    }

}
