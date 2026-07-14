package com.turisGo.bdd.security;

import java.util.Arrays;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turisGo.bdd.model.UserType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Intercepta toda requisição e verifica a anotação {@link RequireRole}:
 * - Endpoint sem @RequireRole -> público, passa direto.
 * - Endpoint com @RequireRole, mas sem sessão válida -> 401.
 * - Endpoint com @RequireRole, sessão válida mas tipo de usuário não permitido -> 403.
 *
 * A anotação no método tem prioridade sobre a anotação na classe.
 */
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }

        if (requireRole == null) {
            return true; // endpoint público
        }

        var currentUserOpt = CurrentUser.from(request);
        if (currentUserOpt.isEmpty()) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Autenticação necessária. Faça login.");
            return false;
        }

        UserType userType = currentUserOpt.get().getType();
        boolean allowed = Arrays.asList(requireRole.value()).contains(userType);
        if (!allowed) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Acesso negado para o tipo de usuário atual (" + userType + ").");
            return false;
        }

        return true;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(java.util.Map.of("erro", message)));
    }
}
