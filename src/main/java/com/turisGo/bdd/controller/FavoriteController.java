package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.Trail;
import com.turisGo.bdd.model.TouristAttraction;
import com.turisGo.bdd.repository.FavoriteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tourists/{touristId}/favorites")
public class FavoriteController {
    private final FavoriteRepository favoriteRepository;

    public FavoriteController(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @PostMapping("/trails/{trailId}")
    public Map<String, Boolean> toggleTrail(@PathVariable int touristId, @PathVariable int trailId) {
        boolean favorited = favoriteRepository.toggleFavoriteTrail(touristId, trailId);
        return Map.of("favorited", favorited);
    }

    @GetMapping("/trails")
    public List<Trail> listFavoriteTrails(@PathVariable int touristId) {
        return favoriteRepository.listFavoriteTrails(touristId);
    }

    @PostMapping("/attractions/{attractionId}")
    public Map<String, Boolean> toggleAttraction(@PathVariable int touristId, @PathVariable int attractionId) {
        boolean favorited = favoriteRepository.toggleFavoriteAttraction(touristId, attractionId);
        return Map.of("favorited", favorited);
    }

    @GetMapping("/attractions")
    public List<TouristAttraction> listFavoriteAttractions(@PathVariable int touristId) {
        return favoriteRepository.listFavoriteAttractions(touristId);
    }
}