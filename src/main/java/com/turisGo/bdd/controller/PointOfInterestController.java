package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.PointOfInterest;
import com.turisGo.bdd.repository.PointOfInterestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PointOfInterestController {
    private final PointOfInterestRepository poiRepository;

    public PointOfInterestController(PointOfInterestRepository poiRepository) {
        this.poiRepository = poiRepository;
    }

    @PostMapping("/points-of-interest")
    public PointOfInterest create(@RequestBody PointOfInterest poi) {
        return poiRepository.save(poi);
    }

    @GetMapping("/points-of-interest/{id}")
    public ResponseEntity<PointOfInterest> getById(@PathVariable int id) {
        return poiRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/attractions/{attractionId}/points-of-interest")
    public List<PointOfInterest> getByAttraction(@PathVariable int attractionId) {
        return poiRepository.findByAttraction(attractionId);
    }
}