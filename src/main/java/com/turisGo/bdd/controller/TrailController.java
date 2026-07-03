package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.Trail;
import com.turisGo.bdd.model.TouristAttraction;
import com.turisGo.bdd.repository.TrailRepository;
import com.turisGo.bdd.repository.TrailAttractionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TrailController {
    private final TrailRepository trailRepository;
    private final TrailAttractionRepository trailAttractionRepository;

    public TrailController(TrailRepository trailRepository, TrailAttractionRepository trailAttractionRepository) {
        this.trailRepository = trailRepository;
        this.trailAttractionRepository = trailAttractionRepository;
    }

    @PostMapping("/trails")
    public Trail create(@RequestBody Trail trail) {
        return trailRepository.save(trail);
    }

    @GetMapping("/trails")
    public List<Trail> getAll() {
        return trailRepository.findAll();
    }

    @GetMapping("/trails/{id}")
    public ResponseEntity<Trail> getById(@PathVariable int id) {
        return trailRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/itineraries/{itineraryId}/trails")
    public List<Trail> getByItinerary(@PathVariable int itineraryId) {
        return trailRepository.findByItinerary(itineraryId);
    }

    @PostMapping("/trails/{trailId}/attractions/{attractionId}")
    public ResponseEntity<String> link(@PathVariable int trailId, @PathVariable int attractionId) {
        boolean linked = trailAttractionRepository.link(trailId, attractionId);
        return linked ? ResponseEntity.ok("Trilha vinculada ao ponto turístico.")
                : ResponseEntity.status(409).body("Vínculo já existe.");
    }

    @DeleteMapping("/trails/{trailId}/attractions/{attractionId}")
    public ResponseEntity<String> unlink(@PathVariable int trailId, @PathVariable int attractionId) {
        trailAttractionRepository.unlink(trailId, attractionId);
        return ResponseEntity.ok("Vínculo removido.");
    }

    @GetMapping("/trails/{trailId}/attractions")
    public List<TouristAttraction> getAttractionsForTrail(@PathVariable int trailId) {
        return trailAttractionRepository.listAttractionsForTrail(trailId);
    }

    @GetMapping("/attractions/{attractionId}/trails")
    public List<Trail> getTrailsForAttraction(@PathVariable int attractionId) {
        return trailAttractionRepository.listTrailsForAttraction(attractionId);
    }
}