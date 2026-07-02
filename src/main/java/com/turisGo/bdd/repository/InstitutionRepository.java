package com.turisGo.bdd.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.turisGo.bdd.model.Institution;

@Repository
public class InstitutionRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String JOIN_SELECT = "SELECT u.*, i.cnpj FROM users u JOIN institutions i ON u.id = i.user_id";

    private static final RowMapper<Institution> INSTITUTION_ROW_MAPPER = (rs, rowNum) -> {
        Institution institution = new Institution();
        institution.setId(rs.getInt("id"));
        institution.setName(rs.getString("name"));
        institution.setEmail(rs.getString("email"));
        institution.setPassword(rs.getString("password"));
        institution.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
        institution.setCnpj(rs.getString("cnpj"));
        return institution;
    };

    public InstitutionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void saveInstitution(Institution institution) {
        String sqlUser = "INSERT INTO users(name, email, password, registration_date) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        institution.setRegistrationDate(LocalDate.now());

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, institution.getName());
            ps.setString(2, institution.getEmail());
            ps.setString(3, institution.getPassword());
            ps.setDate(4, Date.valueOf(institution.getRegistrationDate()));
            return ps;
        }, keyHolder);

        Number userId = keyHolder.getKey();

        if (userId != null) {
            institution.setId(userId.intValue());
            jdbcTemplate.update("INSERT INTO institutions (user_id, cnpj) VALUES (?, ?)", userId.intValue(),
                    institution.getCnpj());
        }
    }

    public List<Institution> findAll() {
        return jdbcTemplate.query(JOIN_SELECT, INSTITUTION_ROW_MAPPER);
    }

    public Optional<Institution> findById(int id) {
        List<Institution> result = jdbcTemplate.query(JOIN_SELECT + " WHERE u.id = ?", INSTITUTION_ROW_MAPPER, id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

}
