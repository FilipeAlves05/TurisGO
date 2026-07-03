package com.turisGo.bdd.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.turisGo.bdd.model.Achievement;

@Repository
public class AchievementRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Achievement> ROW_MAPPER = (rs, rowNum) -> {
        Achievement achievement = new Achievement();
        achievement.setId(rs.getInt("achievement_id"));
        achievement.setName(rs.getString("name"));
        achievement.setDescription(rs.getString("description"));
        achievement.setIcon(rs.getString("icon"));
        return achievement;
    };

    public AchievementRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Achievement save(Achievement achievement) {
        String sql = "INSERT INTO achievements(name, description, icon) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, achievement.getName());
            ps.setString(2, achievement.getDescription());
            ps.setString(3, achievement.getIcon());
            return ps;
        }, keyHolder);

        Number id = keyHolder.getKey();

        if (id != null) {
            achievement.setId(id.intValue());
        }
        return achievement;
    }

    public List<Achievement> findAll() {
        return jdbcTemplate.query("SELECT * FROM achievements", ROW_MAPPER);
    }

    public Optional<Achievement> findById(int id) {
        List<Achievement> result = jdbcTemplate.query("SELECT * FROM achievements WHERE achievement_id = ?", ROW_MAPPER,
                id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public Optional<Achievement> findByName(String name) {
        List<Achievement> result = jdbcTemplate.query("SELECT * FROM achievements WHERE name = ?", ROW_MAPPER, name);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}
