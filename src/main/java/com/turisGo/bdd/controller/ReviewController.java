package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.Review;
import com.turisGo.bdd.model.UserType;
import com.turisGo.bdd.repository.ReviewRepository;
import com.turisGo.bdd.security.CurrentUser;
import com.turisGo.bdd.security.RequireRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@RestController
public class ReviewController {
    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // Relação "avalia" (N:M) é exclusiva do Turista.
    @RequireRole(UserType.TOURIST)
    @PostMapping("/reviews")
    public ResponseEntity<?> upsert(@RequestBody Review review, HttpServletRequest request) {
        Integer currentId = CurrentUser.from(request).map(CurrentUser::getId).orElse(null);
        if (review.getTouristId() == null || !review.getTouristId().equals(currentId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("erro", "Só é possível publicar avaliações com a própria conta de turista."));
        }
        return ResponseEntity.ok(reviewRepository.upsert(review));
    }

    // Leitura de avaliações de um ponto turístico é pública (ajuda outros turistas a decidir a visita).
    @GetMapping("/attractions/{attractionId}/reviews")
    public List<Review> getByAttraction(@PathVariable int attractionId) {
        return reviewRepository.findByAttraction(attractionId);
    }

    @GetMapping("/attractions/{attractionId}/reviews/average")
    public Map<String, Object> getAverage(@PathVariable int attractionId) {
        Double avg = reviewRepository.getAverageRating(attractionId);
        return Map.of("attractionId", attractionId, "averageRating", avg == null ? 0 : avg);
    }

    // Histórico de avaliações feitas pelo próprio turista.
    @RequireRole(UserType.TOURIST)
    @GetMapping("/tourists/{touristId}/reviews")
    public ResponseEntity<?> getByTourist(@PathVariable int touristId, HttpServletRequest request) {
        Integer currentId = CurrentUser.from(request).map(CurrentUser::getId).orElse(null);
        if (currentId == null || !currentId.equals(touristId)) {
            return ResponseEntity.status(403).body(Map.of("erro", "Você só pode ver as próprias avaliações."));
        }
        return ResponseEntity.ok(reviewRepository.findByTourist(touristId));
    }
}