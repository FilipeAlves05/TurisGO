package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.CheckIn;
import com.turisGo.bdd.model.UserType;
import com.turisGo.bdd.repository.CheckInRepository;
import com.turisGo.bdd.security.CurrentUser;
import com.turisGo.bdd.security.RequireRole;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import com.turisGo.bdd.dto.CheckInRequest;
import com.turisGo.bdd.dto.CheckInResult;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/checkins")
public class CheckInController {
    private final CheckInRepository checkInRepository;

    public CheckInController(CheckInRepository checkInRepository) {
        this.checkInRepository = checkInRepository;
    }

    // Quem realiza o Check-in é sempre o próprio Turista (relação "realiza").
    @RequireRole(UserType.TOURIST)
    @PostMapping
    public ResponseEntity<?> checkIn(@RequestBody CheckInRequest request, HttpServletRequest httpRequest) {
        Integer currentId = CurrentUser.from(httpRequest).map(CurrentUser::getId).orElse(null);
        if (request.getTouristId() == null || !request.getTouristId().equals(currentId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("erro", "Só é possível registrar check-in para a própria conta de turista."));
        }
        try {
            CheckInResult result = checkInRepository.performCheckIn(request);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    // Visão geral de check-ins: só as Instituições, que são quem "valida" os check-ins.
    @RequireRole(UserType.INSTITUTION)
    @GetMapping
    public List<CheckIn> getAll() {
        return checkInRepository.findAll();
    }

    @RequireRole(UserType.INSTITUTION)
    @GetMapping("/{id}")
    public ResponseEntity<CheckIn> getById(@PathVariable int id) {
        return checkInRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Histórico de check-ins do próprio turista.
    @RequireRole(UserType.TOURIST)
    @GetMapping("/tourists/{touristId}")
    public ResponseEntity<?> getByTourist(@PathVariable int touristId, HttpServletRequest request) {
        Integer currentId = CurrentUser.from(request).map(CurrentUser::getId).orElse(null);
        if (currentId == null || !currentId.equals(touristId)) {
            return ResponseEntity.status(403).body(Map.of("erro", "Você só pode ver os próprios check-ins."));
        }
        return ResponseEntity.ok(checkInRepository.findByTourist(touristId));
    }

    // Check-ins recebidos em um ponto turístico: só a instituição responsável.
    @RequireRole(UserType.INSTITUTION)
    @GetMapping("/attractions/{attractionId}")
    public List<CheckIn> getByAttraction(@PathVariable int attractionId) {
        return checkInRepository.findByAttraction(attractionId);
    }
}
