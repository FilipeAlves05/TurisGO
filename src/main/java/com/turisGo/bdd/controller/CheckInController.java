package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.CheckIn;
import com.turisGo.bdd.repository.CheckInRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import com.turisGo.bdd.dto.CheckInRequest;
import com.turisGo.bdd.dto.CheckInResult;

@RestController
@RequestMapping("/checkins")
public class CheckInController {
    private final CheckInRepository checkInRepository;

    public CheckInController(CheckInRepository checkInRepository) {
        this.checkInRepository = checkInRepository;
    }

    @PostMapping
    public ResponseEntity<?> checkIn(@RequestBody CheckInRequest request) {
        try {
            CheckInResult result = checkInRepository.performCheckIn(request);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @GetMapping
    public List<CheckIn> getAll() {
        return checkInRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckIn> getById(@PathVariable int id) {
        return checkInRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tourists/{touristId}")
    public List<CheckIn> getByTourist(@PathVariable int touristId) {
        return checkInRepository.findByTourist(touristId);
    }

    @GetMapping("/attractions/{attractionId}")
    public List<CheckIn> getByAttraction(@PathVariable int attractionId) {
        return checkInRepository.findByAttraction(attractionId);
    }
}
