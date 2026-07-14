package com.turisGo.bdd.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turisGo.bdd.dto.AuthResponse;
import com.turisGo.bdd.dto.LoginRequest;
import com.turisGo.bdd.model.Institution;
import com.turisGo.bdd.model.Tourist;
import com.turisGo.bdd.repository.AuthRepository;
import com.turisGo.bdd.repository.InstitutionRepository;
import com.turisGo.bdd.repository.TouristRepository;
import com.turisGo.bdd.security.CurrentUser;
import com.turisGo.bdd.security.SessionKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TouristRepository touristRepository;
    private final InstitutionRepository institutionRepository;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(TouristRepository touristRepository, InstitutionRepository institutionRepository,
            AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.touristRepository = touristRepository;
        this.institutionRepository = institutionRepository;
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/registro/turista")
    public ResponseEntity<?> registerTourist(@RequestBody Tourist tourist) {
        try {
            touristRepository.saveTourist(tourist);
            tourist.setPassword(null);
            return ResponseEntity.status(201).body(tourist);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/registro/instituicao")
    public ResponseEntity<?> registerInstitution(@RequestBody Institution institution) {
        try {
            institutionRepository.saveInstitution(institution);
            institution.setPassword(null);
            return ResponseEntity.status(201).body(institution);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        var authRowOpt = authRepository.findByEmail(request.getEmail());

        boolean invalid = authRowOpt.isEmpty()
                || authRowOpt.get().getType() == null
                || !passwordEncoder.matches(request.getPassword(), authRowOpt.get().getPasswordHash());

        if (invalid) {
            return ResponseEntity.status(401).body(Map.of("erro", "E-mail ou senha inválidos."));
        }

        var authRow = authRowOpt.get();

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(SessionKeys.USER_ID, authRow.getId());
        session.setAttribute(SessionKeys.USER_TYPE, authRow.getType());
        session.setAttribute(SessionKeys.USER_NAME, authRow.getName());
        session.setAttribute(SessionKeys.USER_EMAIL, authRow.getEmail());

        return ResponseEntity.ok(new AuthResponse(authRow.getId(), authRow.getName(), authRow.getEmail(),
                authRow.getType()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("mensagem", "Logout realizado com sucesso."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest httpRequest) {
        var currentUserOpt = CurrentUser.from(httpRequest);
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("erro", "Nenhum usuário autenticado."));
        }
        var u = currentUserOpt.get();
        return ResponseEntity.ok(new AuthResponse(u.getId(), u.getName(), u.getEmail(), u.getType()));
    }
}
