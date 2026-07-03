package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.Itinerary;
import com.turisGo.bdd.repository.ItineraryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ItineraryController {
    private final ItineraryRepository itineraryRepository;

    public ItineraryController(ItineraryRepository itineraryRepository) {
        this.itineraryRepository = itineraryRepository;
    }

    @PostMapping("/itineraries")
    public Itinerary create(@RequestBody Itinerary itinerary) {
        return itineraryRepository.save(itinerary);
    }

    @GetMapping("/itineraries")
    public List<Itinerary> getAll() {
        return itineraryRepository.findAll();
    }

    @GetMapping("/itineraries/{id}")
    public ResponseEntity<Itinerary> getById(@PathVariable int id) {
        return itineraryRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tourists/{touristId}/itineraries")
    public List<Itinerary> getByTourist(@PathVariable int touristId) {
        return itineraryRepository.findByTourist(touristId);
    }
}