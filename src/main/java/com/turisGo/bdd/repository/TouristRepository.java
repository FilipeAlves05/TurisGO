package com.turisGo.bdd.repository;

import com.turisGo.bdd.model.Tourist;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Date;

@Repository
public class TouristRepository {
    private final JdbcTemplate jdbcTemplate;

    public TouristRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void saveTourist(Tourist tourist) {
        String sqlUser = "INSERT INTO users (name, email, password, registration_date) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tourist.getName());
            ps.setString(2, tourist.getEmail());
            ps.setString(3, tourist.getPassword());
            ps.setDate(4, Date.valueOf(tourist.getRegistrationDate()));
            return ps;
        }, keyHolder);

        Number userId = keyHolder.getKey();

        if (userId != null) {
            String sqlTourist = "INSERT INTO tourists (user_id, birth_date, total_points, level) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sqlTourist, userId.intValue(), Date.valueOf(tourist.getBirthDate()), 0, 1);
        }
    }
}
