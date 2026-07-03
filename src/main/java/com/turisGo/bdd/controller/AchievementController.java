package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.Achievement;
import com.turisGo.bdd.repository.AchievementRepository;
import com.turisGo.bdd.repository.TouristAchievementRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
public class AchievementController {
    private final AchievementRepository achievementRepository;
    private final TouristAchievementRepository touristAchievementRepository;

    public AchievementController(AchievementRepository achievementRepository,
            TouristAchievementRepository touristAchievementRepository) {
        this.achievementRepository = achievementRepository;
        this.touristAchievementRepository = touristAchievementRepository;
    }

    @PostMapping("/achievements")
    public Achievement create(@RequestBody Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    @GetMapping("/achievements")
    public List<Achievement> getAll() {
        return achievementRepository.findAll();
    }

    @GetMapping("/achievements/{id}")
    public ResponseEntity<Achievement> getById(@PathVariable int id) {
        return achievementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/tourists/{touristId}/achievements/{achievementId}")
    public ResponseEntity<String> grant(@PathVariable int touristId, @PathVariable int achievementId) {
        boolean granted = touristAchievementRepository.grant(touristId, achievementId);
        if (granted) {
            return ResponseEntity.ok("Conquista concedida com sucesso.");
        } else {
            return ResponseEntity.badRequest().body("O turista já possui essa conquista.");
        }
    }

    @GetMapping("/tourists/{touristId}/achievements")
    public List<Achievement> listByTourist(@PathVariable int touristId) {
        return touristAchievementRepository.listByTourist(touristId);
    }

}
