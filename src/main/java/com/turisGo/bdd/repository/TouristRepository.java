package com.turisGo.bdd.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.RowMapper;

import com.turisGo.bdd.model.Tourist;

@Repository
public class TouristRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String JOIN_SELECT = "SELECT u.*m t.birth_date, t.total_points, t.level FROM users u JOIN tourists t ON u.id = t.user_id";

    private static final RowMapper<Tourist> TOURIST_ROW_MAPPER = (rs, rowNum) -> {
        Tourist tourist = new Tourist();
        tourist.setId(rs.getInt("id"));
        tourist.setName(rs.getString("name"));
        tourist.setEmail(rs.getString("email"));
        tourist.setPassword(rs.getString("password"));
        tourist.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
        tourist.setBirthDate(rs.getDate("birth_date").toLocalDate());
        tourist.setTotalPoints(rs.getInt("total_points"));
        tourist.setLevel(rs.getInt("level"));
        return tourist;
    };

    public TouristRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void saveTourist(Tourist tourist) {
        String sqlUser = "INSERT INTO users (name, email, password, registration_date) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        tourist.setRegistrationDate(LocalDate.now());

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
            tourist.setId(userId.intValue());
            String sqlTourist = "INSERT INTO tourists (user_id, birth_date, total_points, level) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sqlTourist, userId.intValue(), Date.valueOf(tourist.getBirthDate()), 0, 1);
        }
    }

    public List<Tourist> findAll() {
        return jdbcTemplate.query(JOIN_SELECT, TOURIST_ROW_MAPPER);
    }

    public Optional<Tourist> findById(int id) {
        List<Tourist> result = jdbcTemplate.query(JOIN_SELECT + " WHERE u.id = ?", TOURIST_ROW_MAPPER, id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    public void addPoints(int touristId, int amount) {
        String sql = "UPDATE tourists SET total_points = total_points + ?, "
                + "level = 1 +FLOOR((total_points + ?) / 100 WHERE user_id = ?";
        jdbcTemplate.update(sql, amount, amount, touristId);
    }
}
