package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.Itinerary;
import com.turisGo.bdd.model.UserType;
import com.turisGo.bdd.repository.ItineraryRepository;
import com.turisGo.bdd.security.CurrentUser;
import com.turisGo.bdd.security.RequireRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@RestController
public class ItineraryController {
    private final ItineraryRepository itineraryRepository;

    public ItineraryController(ItineraryRepository itineraryRepository) {
        this.itineraryRepository = itineraryRepository;
    }

    // Um roteiro (Roteiro) sempre pertence a um Turista (relação "Gerencia" 1:N).
    @RequireRole(UserType.TOURIST)
    @PostMapping("/itineraries")
    public ResponseEntity<?> create(@RequestBody Itinerary itinerary, HttpServletRequest request) {
        Integer currentId = CurrentUser.from(request).map(CurrentUser::getId).orElse(null);
        if (itinerary.getTouristId() == null || !itinerary.getTouristId().equals(currentId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("erro", "Só é possível criar roteiros para a própria conta de turista."));
        }
        return ResponseEntity.ok(itineraryRepository.save(itinerary));
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