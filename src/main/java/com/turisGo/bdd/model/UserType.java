package com.turisGo.bdd.model;

/**
 * Discrimina o subtipo do Usuario (ver "d" - subtipo disjunto - no modelo ER):
 * cada Usuario é OU um Turista OU uma Instituicao, nunca os dois.
 */
public enum UserType {
    TOURIST,
    INSTITUTION
}
