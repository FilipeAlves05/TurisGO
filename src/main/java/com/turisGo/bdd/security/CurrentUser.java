package com.turisGo.bdd.security;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.turisGo.bdd.model.UserType;

/**
 * Lê os dados do usuário autenticado (guardados na sessão HTTP no login)
 * para uso dentro dos controllers, por exemplo para checar se o usuário
 * logado é "dono" do recurso que está tentando alterar.
 */
public final class CurrentUser {

    private final Integer id;
    private final UserType type;
    private final String name;
    private final String email;

    private CurrentUser(Integer id, UserType type, String name, String email) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.email = email;
    }

    public static Optional<CurrentUser> from(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionKeys.USER_ID) == null) {
            return Optional.empty();
        }
        Integer id = (Integer) session.getAttribute(SessionKeys.USER_ID);
        UserType type = (UserType) session.getAttribute(SessionKeys.USER_TYPE);
        String name = (String) session.getAttribute(SessionKeys.USER_NAME);
        String email = (String) session.getAttribute(SessionKeys.USER_EMAIL);
        return Optional.of(new CurrentUser(id, type, name, email));
    }

    public Integer getId() {
        return id;
    }

    public UserType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isTourist() {
        return type == UserType.TOURIST;
    }

    public boolean isInstitution() {
        return type == UserType.INSTITUTION;
    }

    /** Verifica se o usuário logado é o próprio dono do id informado. */
    public boolean isOwner(Integer resourceOwnerId) {
        return resourceOwnerId != null && resourceOwnerId.equals(this.id);
    }
}
