package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.Review;
import com.turisGo.bdd.repository.ReviewRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ReviewController {
    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @PostMapping("/reviews")
    public Review upsert(@RequestBody Review review) {
        return reviewRepository.upsert(review);
    }

    @GetMapping("/attractions/{attractionId}/reviews")
    public List<Review> getByAttraction(@PathVariable int attractionId) {
        return reviewRepository.findByAttraction(attractionId);
    }

    @GetMapping("/attractions/{attractionId}/reviews/average")
    public Map<String, Object> getAverage(@PathVariable int attractionId) {
        Double avg = reviewRepository.getAverageRating(attractionId);
        return Map.of("attractionId", attractionId, "averageRating", avg == null ? 0 : avg);
    }

    @GetMapping("/tourists/{touristId}/reviews")
    public List<Review> getByTourist(@PathVariable int touristId) {
        return reviewRepository.findByTourist(touristId);
    }
}