package com.turisGo.bdd.controller;

import com.turisGo.bdd.model.User;
import com.turisGo.bdd.model.UserType;
import com.turisGo.bdd.dto.ContactRequest;
import com.turisGo.bdd.repository.UserRepository;
import com.turisGo.bdd.security.CurrentUser;
import com.turisGo.bdd.security.RequireRole;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Consulta administrativa: mantida pública apenas para fins de correção/depuração do BD.
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        return userRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Contatos são dado sensível do perfil: só o próprio dono (turista ou instituição) pode ver/alterar.
    @RequireRole({ UserType.TOURIST, UserType.INSTITUTION })
    @GetMapping("/{id}/contacts")
    public ResponseEntity<?> getContacts(@PathVariable int id, HttpServletRequest request) {
        return forbiddenUnlessOwner(id, request, () -> ResponseEntity.ok(userRepository.listContacts(id)));
    }

    @RequireRole({ UserType.TOURIST, UserType.INSTITUTION })
    @PostMapping("/{id}/contacts")
    public ResponseEntity<?> addContact(@PathVariable int id, @RequestBody ContactRequest request,
            HttpServletRequest httpRequest) {
        return forbiddenUnlessOwner(id, httpRequest, () -> {
            userRepository.addContact(id, request.getContact());
            return ResponseEntity.ok("Contato adicionado.");
        });
    }

    @RequireRole({ UserType.TOURIST, UserType.INSTITUTION })
    @DeleteMapping("/{id}/contacts/{contact}")
    public ResponseEntity<?> removeContact(@PathVariable int id, @PathVariable String contact,
            HttpServletRequest httpRequest) {
        return forbiddenUnlessOwner(id, httpRequest, () -> {
            userRepository.removeContact(id, contact);
            return ResponseEntity.ok("Contato removido.");
        });
    }

    private ResponseEntity<?> forbiddenUnlessOwner(int id, HttpServletRequest request,
            java.util.function.Supplier<ResponseEntity<?>> action) {
        boolean isOwner = CurrentUser.from(request).map(u -> u.isOwner(id)).orElse(false);
        if (!isOwner) {
            return ResponseEntity.status(403).body(Map.of("erro", "Você só pode acessar os contatos da própria conta."));
        }
        return action.get();
    }
}
