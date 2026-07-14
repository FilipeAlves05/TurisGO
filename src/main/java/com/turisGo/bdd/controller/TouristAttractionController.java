package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.TouristAttraction;
import com.turisGo.bdd.model.UserType;
import com.turisGo.bdd.repository.TouristAttractionRepository;
import com.turisGo.bdd.security.CurrentUser;
import com.turisGo.bdd.security.RequireRole;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@RestController
public class TouristAttractionController {
    private final TouristAttractionRepository touristAttractionRepository;

    public TouristAttractionController(TouristAttractionRepository touristAttractionRepository) {
        this.touristAttractionRepository = touristAttractionRepository;
    }

    // Ponto Turístico é gerenciado por uma Instituicao (relação "Gerencia" 1:N).
    @RequireRole(UserType.INSTITUTION)
    @PostMapping("/attractions")
    public ResponseEntity<?> create(@RequestBody TouristAttraction attraction, HttpServletRequest request) {
        Integer currentId = CurrentUser.from(request).map(CurrentUser::getId).orElse(null);
        if (attraction.getInstitutionId() == null || !attraction.getInstitutionId().equals(currentId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("erro", "Só é possível cadastrar pontos turísticos para a própria instituição."));
        }
        return ResponseEntity.ok(touristAttractionRepository.save(attraction));
    }

    @RequireRole(UserType.INSTITUTION)
    @PutMapping("/attractions/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody TouristAttraction attraction,
            HttpServletRequest request) {
        Integer currentId = CurrentUser.from(request).map(CurrentUser::getId).orElse(null);
        var existing = touristAttractionRepository.findById(id);
        if (existing.isEmpty() || !existing.get().getInstitutionId().equals(currentId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("erro", "Você só pode editar pontos turísticos da própria instituição."));
        }
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
