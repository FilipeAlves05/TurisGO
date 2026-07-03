package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.Institution;
import com.turisGo.bdd.repository.InstitutionRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/institutions")
public class InstitutionController {
    private final InstitutionRepository institutionRepository;

    public InstitutionController(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    @PostMapping
    public String createInstitution(@RequestBody Institution institution) {
        institutionRepository.saveInstitution(institution);
        return "Instituição cadastrada com sucesso nas tabelas users e institutions.";
    }

    @GetMapping
    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Institution> getInstitution(@PathVariable int id) {
        return institutionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
