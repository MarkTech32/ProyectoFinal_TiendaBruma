package com.bruma.service;

import com.bruma.repository.ReviewRepository;
import com.bruma.domain.Review;
import com.bruma.repository.ReviewRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Transactional(readOnly = true)
    public List<Review> getReviews() {
        return reviewRepository.findByActivoTrue();
    }
    
    @Transactional(readOnly = true)
    public List<Review> getActiveReviewsSorted() {
        return reviewRepository.findByActivoTrueOrderByFechaCreacionDesc();
    }
    
    @Transactional(readOnly = true)
    public Review getReview(Long idReview) {
        return reviewRepository.findById(idReview).orElse(null);
    }
    
    @Transactional
    public void save(Review review) {
        reviewRepository.save(review);
    }
    
    @Transactional
    public void delete(Review review) {
        reviewRepository.delete(review);
    }
    
    @Transactional(readOnly = true)
    public List<Review> findByUserId(Long idUsuario) {
        return reviewRepository.findByUsuarioIdUsuarioAndActivoTrue(idUsuario);
    }
    
    @Transactional(readOnly = true)
    public List<Review> findByMinRating(int minRating) {
        return reviewRepository.findByRatingGreaterThanEqualAndActivoTrue(minRating);
    }
}
