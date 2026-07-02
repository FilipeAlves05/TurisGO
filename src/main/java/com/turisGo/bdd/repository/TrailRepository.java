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

import com.turisGo.bdd.model.Trail;

@Repository
public class TrailRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Trail> ROW_MAPPER = (rs, rowNum) -> {
        Trail t = new Trail();
        t.setId(rs.getInt("trail_id"));
        t.setName(rs.getString("name"));
        t.setDescription(rs.getString("description"));
        t.setDifficulty(rs.getString("difficulty"));
        t.setCategory(rs.getString("category"));
        t.setEstimatedTime(rs.getString("estimated_time"));
        t.setRewardPoints(rs.getObject("reward_points", Integer.class));
        t.setItineraryId(rs.getInt("itinerary_id"));
        return t;
    };

    public TrailRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Trail save(Trail trail) {
        String sql = "INSERT INTO trails (name, description, difficulty, category, estimated_time, reward_points, itinerary_id) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, trail.getName());
            ps.setString(2, trail.getDescription());
            ps.setString(3, trail.getDifficulty());
            ps.setString(4, trail.getCategory());
            ps.setString(5, trail.getEstimatedTime());
            if (trail.getRewardPoints() != null)
                ps.setInt(6, trail.getRewardPoints());
            else
                ps.setNull(6, java.sql.Types.INTEGER);
            ps.setInt(7, trail.getItineraryId());
            return ps;
        }, keyHolder);
        Number id = keyHolder.getKey();
        if (id != null)
            trail.setId(id.intValue());
        return trail;
    }

    public List<Trail> findAll() {
        return jdbcTemplate.query("SELECT * FROM trails", ROW_MAPPER);
    }

    public Optional<Trail> findById(int id) {
        List<Trail> result = jdbcTemplate.query("SELECT * FROM trails WHERE trail_id = ?", ROW_MAPPER, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<Trail> findByItinerary(int itineraryId) {
        return jdbcTemplate.query("SELECT * FROM trails WHERE itinerary_id = ?", ROW_MAPPER, itineraryId);
    }
}