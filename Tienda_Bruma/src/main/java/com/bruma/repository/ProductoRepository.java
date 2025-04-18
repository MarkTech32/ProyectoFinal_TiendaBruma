package com.bruma.repository;

import com.bruma.domain.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
    // Método para encontrar productos por ID de categoría
    List<Producto> findByCategoria_IdCategoria(Long idCategoria);
    
    // Método para encontrar productos activos
    List<Producto> findByActivoTrue();
    
    // Método para encontrar productos por ID de categoría y que estén activos
    List<Producto> findByCategoria_IdCategoriaAndActivoTrue(Long idCategoria);
}