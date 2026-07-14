package com.turisGo.bdd.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.turisGo.bdd.model.UserType;

/**
 * Marca um endpoint como restrito a um ou mais tipos de usuário
 * (TOURIST e/ou INSTITUTION). Sem esta anotação, o endpoint é público.
 *
 * Pode ser usada tanto na classe (aplica a todos os métodos do controller)
 * quanto em um método específico (o método tem prioridade sobre a classe).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface RequireRole {
    UserType[] value();
}
