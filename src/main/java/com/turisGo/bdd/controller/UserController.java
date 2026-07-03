package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.User;
import com.turisGo.bdd.dto.ContactRequest;
import com.turisGo.bdd.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        return userRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/contacts")
    public List<String> getContacts(@PathVariable int id) {
        return userRepository.listContacts(id);
    }

    @PostMapping("/{id}/contacts")
    public ResponseEntity<String> addContact(@PathVariable int id, @RequestBody ContactRequest request) {
        userRepository.addContact(id, request.getContact());
        return ResponseEntity.ok("Contato adicionado.");
    }

    @DeleteMapping("/{id}/contacts/{contact}")
    public ResponseEntity<String> removeContact(@PathVariable int id, @PathVariable String contact) {
        userRepository.removeContact(id, contact);
        return ResponseEntity.ok("Contato removido.");
    }
}
