package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.TouristAttraction;
import com.turisGo.bdd.repository.TouristAttractionRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
public class TouristAttractionController {
    private final TouristAttractionRepository touristAttractionRepository;

    public TouristAttractionController(TouristAttractionRepository touristAttractionRepository) {
        this.touristAttractionRepository = touristAttractionRepository;
    }

    @PostMapping("/attractions")
    public TouristAttraction create(@RequestBody TouristAttraction attraction) {
        return touristAttractionRepository.save(attraction);
    }

    @PutMapping("/attractions/{id}")
    public ResponseEntity<String> update(@PathVariable int id, @RequestBody TouristAttraction attraction) {
        attraction.setId(id);
        touristAttractionRepository.update(attraction);
        return ResponseEntity.ok("Ponto turístico atualizado.");
    }

    @GetMapping("/attractions")
    public List<TouristAttraction> getAll() {
        return touristAttractionRepository.findAll();
    }

    @GetMapping("/attractions/{id}")
    public ResponseEntity<TouristAttraction> getById(@PathVariable int id) {
        return touristAttractionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/institutions/{institutionId}/attractions")
    public List<TouristAttraction> getByInstitution(@PathVariable int institutionId) {
        return touristAttractionRepository.findByInstitution(institutionId);
    }
}
