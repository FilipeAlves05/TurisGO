package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.Tourist;
import com.turisGo.bdd.model.UserType;
import com.turisGo.bdd.repository.TouristRepository;
import com.turisGo.bdd.security.RequireRole;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/tourists")
public class TouristController {
    private final TouristRepository touristRepository;

    public TouristController(TouristRepository touristRepository) {
        this.touristRepository = touristRepository;
    }

    // Cadastro público. Equivalente a POST /auth/registro/turista.
    @PostMapping
    public String createTourist(@RequestBody Tourist tourist) {
        touristRepository.saveTourist(tourist);
        return "Turista cadastrado com sucesso nas tabelas users e tourists.";
    }

    // Lista completa de turistas: apenas Instituições (ex.: telas administrativas/estatísticas).
    @RequireRole(UserType.INSTITUTION)
    @GetMapping
    public List<Tourist> getAllTourist() {
        return touristRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tourist> getTourist(@PathVariable int id) {
        return touristRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
