package com.turisGo.bdd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.turisGo.bdd.model.UserType;

/**
 * Repositório de apoio ao login: descobre, a partir do e-mail, se o Usuario é
 * um Turista ou uma Instituicao (subtipo disjunto "d" do modelo ER) e traz o
 * hash da senha para validação.
 */
@Repository
public class AuthRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String SQL = "SELECT u.id, u.name, u.email, u.password, "
            + "CASE WHEN t.user_id IS NOT NULL THEN 'TOURIST' "
            + "     WHEN i.user_id IS NOT NULL THEN 'INSTITUTION' "
            + "     ELSE NULL END AS user_type "
            + "FROM users u "
            + "LEFT JOIN tourists t ON t.user_id = u.id "
            + "LEFT JOIN institutions i ON i.user_id = u.id ";

    private static final RowMapper<AuthRow> ROW_MAPPER = (rs, rowNum) -> {
        String type = rs.getString("user_type");
        return new AuthRow(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                type == null ? null : UserType.valueOf(type));
    };

    public AuthRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AuthRow> findByEmail(String email) {
        List<AuthRow> result = jdbcTemplate.query(SQL + "WHERE u.email = ?", ROW_MAPPER, email);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public boolean emailExists(String email) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Integer.class,
                email);
        return count != null && count > 0;
    }

    /** Linha de autenticação: dados mínimos de users + o tipo descoberto. */
    public static class AuthRow {
        private final Integer id;
        private final String name;
        private final String email;
        private final String passwordHash;
        private final UserType type;

        public AuthRow(Integer id, String name, String email, String passwordHash, UserType type) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.passwordHash = passwordHash;
            this.type = type;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPasswordHash() {
            return passwordHash;
        }

        public UserType getType() {
            return type;
        }
    }
}
