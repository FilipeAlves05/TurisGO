package com.turisGo.bdd.dto;

import com.turisGo.bdd.model.UserType;

/**
 * Retornado no login e no /auth/me. Nunca contém a senha.
 */
public class AuthResponse {
    private Integer id;
    private String name;
    private String email;
    private UserType type;

    public AuthResponse() {
    }

    public AuthResponse(Integer id, String name, String email, UserType type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getType() {
        return this.type;
    }

    public void setType(UserType type) {
        this.type = type;
    }
}
