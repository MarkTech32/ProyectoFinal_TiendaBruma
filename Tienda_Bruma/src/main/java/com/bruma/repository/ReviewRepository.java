package com.bruma.repository;

import com.bruma.domain.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Método para obtener todas las reseñas activas
    List<Review> findByActivoTrue();
    
    // Método para obtener reseñas activas ordenadas por fecha (las más recientes primero)
    List<Review> findByActivoTrueOrderByFechaCreacionDesc();
    
    // Método para buscar reseñas por usuario
    List<Review> findByUsuarioIdUsuarioAndActivoTrue(Long idUsuario);
    
    // Método para buscar reseñas con una calificación mínima
    List<Review> findByRatingGreaterThanEqualAndActivoTrue(int minRating);

    
}
