package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.PointOfInterest;
import com.turisGo.bdd.model.UserType;
import com.turisGo.bdd.repository.PointOfInterestRepository;
import com.turisGo.bdd.repository.TouristAttractionRepository;
import com.turisGo.bdd.security.CurrentUser;
import com.turisGo.bdd.security.RequireRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@RestController
public class PointOfInterestController {
    private final PointOfInterestRepository poiRepository;
    private final TouristAttractionRepository touristAttractionRepository;

    public PointOfInterestController(PointOfInterestRepository poiRepository,
            TouristAttractionRepository touristAttractionRepository) {
        this.poiRepository = poiRepository;
        this.touristAttractionRepository = touristAttractionRepository;
    }

    // Ponto de Interesse é conteúdo do Ponto Turístico (relação "contem"), então só a
    // instituição dona do ponto turístico pode cadastrar pontos de interesse nele.
    @RequireRole(UserType.INSTITUTION)
    @PostMapping("/points-of-interest")
    public ResponseEntity<?> create(@RequestBody PointOfInterest poi, HttpServletRequest request) {
        Integer currentId = CurrentUser.from(request).map(CurrentUser::getId).orElse(null);
        boolean ownsAttraction = poi.getAttractionId() != null
                && touristAttractionRepository.findById(poi.getAttractionId())
                        .map(a -> a.getInstitutionId() != null && a.getInstitutionId().equals(currentId))
                        .orElse(false);
        if (!ownsAttraction) {
            return ResponseEntity.status(403)
                    .body(Map.of("erro", "Você só pode cadastrar pontos de interesse em pontos turísticos da própria instituição."));
        }
        return ResponseEntity.ok(poiRepository.save(poi));
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