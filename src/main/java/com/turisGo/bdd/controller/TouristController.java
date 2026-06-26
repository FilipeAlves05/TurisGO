package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.Tourist;
import com.turisGo.bdd.repository.TouristRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tourists")
public class TouristController {
    private final TouristRepository touristRepository;

    public TouristController(TouristRepository touristRepository) {
        this.touristRepository = touristRepository;
    }

    @PostMapping
    public String createTourist(@RequestBody Tourist tourist) {
        touristRepository.saveTourist(tourist);
        return "Turista cadastrado com sucesso nas tabelas users e tourists.";
    }

}
