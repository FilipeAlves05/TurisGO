package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.Trail;
import com.turisGo.bdd.model.TouristAttraction;
import com.turisGo.bdd.model.UserType;
import com.turisGo.bdd.repository.ItineraryRepository;
import com.turisGo.bdd.repository.TrailRepository;
import com.turisGo.bdd.repository.TrailAttractionRepository;
import com.turisGo.bdd.security.CurrentUser;
import com.turisGo.bdd.security.RequireRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@RestController
public class TrailController {
    private final TrailRepository trailRepository;
    private final TrailAttractionRepository trailAttractionRepository;
    private final ItineraryRepository itineraryRepository;

    public TrailController(TrailRepository trailRepository, TrailAttractionRepository trailAttractionRepository,
            ItineraryRepository itineraryRepository) {
        this.trailRepository = trailRepository;
        this.trailAttractionRepository = trailAttractionRepository;
        this.itineraryRepository = itineraryRepository;
    }

    // Uma trilha (Trilha) é incluída em um Roteiro (relação "Inclui" N:1) que pertence a um Turista.
    @RequireRole(UserType.TOURIST)
    @PostMapping("/trails")
    public ResponseEntity<?> create(@RequestBody Trail trail, HttpServletRequest request) {
        Integer currentId = CurrentUser.from(request).map(CurrentUser::getId).orElse(null);
        boolean ownsItinerary = trail.getItineraryId() != null
                && itineraryRepository.findById(trail.getItineraryId())
                        .map(it -> it.getTouristId().equals(currentId))
                        .orElse(false);
        if (!ownsItinerary) {
            return ResponseEntity.status(403)
                    .body(Map.of("erro", "Você só pode adicionar trilhas a um roteiro seu."));
        }
        return ResponseEntity.ok(trailRepository.save(trail));
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

    // O dono do roteiro (Turista) decide por quais pontos turísticos a trilha passa (relação "pertence").
    @RequireRole(UserType.TOURIST)
    @PostMapping("/trails/{trailId}/attractions/{attractionId}")
    public ResponseEntity<String> link(@PathVariable int trailId, @PathVariable int attractionId) {
        boolean linked = trailAttractionRepository.link(trailId, attractionId);
        return linked ? ResponseEntity.ok("Trilha vinculada ao ponto turístico.")
                : ResponseEntity.status(409).body("Vínculo já existe.");
    }

    @RequireRole(UserType.TOURIST)
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