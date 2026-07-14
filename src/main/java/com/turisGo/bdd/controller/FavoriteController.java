package com.turisGo.bdd.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turisGo.bdd.model.UserType;
import com.turisGo.bdd.repository.FavoriteRepository;
import com.turisGo.bdd.security.CurrentUser;
import com.turisGo.bdd.security.RequireRole;

import jakarta.servlet.http.HttpServletRequest;

@RequireRole(UserType.TOURIST)
@RestController
@RequestMapping("/tourists/{touristId}/favorites")
public class FavoriteController {
    private final FavoriteRepository favoriteRepository;

    public FavoriteController(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @PostMapping("/trails/{trailId}")
    public ResponseEntity<?> toggleTrail(@PathVariable int touristId, @PathVariable int trailId,
            HttpServletRequest request) {
        return withOwnership(touristId, request,
                () -> ResponseEntity.ok(Map.of("favorited", favoriteRepository.toggleFavoriteTrail(touristId, trailId))));
    }

    @GetMapping("/trails")
    public ResponseEntity<?> listFavoriteTrails(@PathVariable int touristId, HttpServletRequest request) {
        return withOwnership(touristId, request,
                () -> ResponseEntity.ok(favoriteRepository.listFavoriteTrails(touristId)));
    }

    @PostMapping("/attractions/{attractionId}")
    public ResponseEntity<?> toggleAttraction(@PathVariable int touristId, @PathVariable int attractionId,
            HttpServletRequest request) {
        return withOwnership(touristId, request, () -> ResponseEntity
                .ok(Map.of("favorited", favoriteRepository.toggleFavoriteAttraction(touristId, attractionId))));
    }

    @GetMapping("/attractions")
    public ResponseEntity<?> listFavoriteAttractions(@PathVariable int touristId, HttpServletRequest request) {
        return withOwnership(touristId, request,
                () -> ResponseEntity.ok(favoriteRepository.listFavoriteAttractions(touristId)));
    }

    private ResponseEntity<?> withOwnership(int touristId, HttpServletRequest request,
            java.util.function.Supplier<ResponseEntity<?>> action) {
        Integer currentId = CurrentUser.from(request).map(CurrentUser::getId).orElse(null);
        if (currentId == null || !currentId.equals(touristId)) {
            return ResponseEntity.status(403).body(Map.of("erro", "Você só pode gerenciar os próprios favoritos."));
        }
        return action.get();
    }
}