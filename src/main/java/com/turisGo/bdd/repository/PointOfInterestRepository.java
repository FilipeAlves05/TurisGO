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

import com.turisGo.bdd.model.PointOfInterest;

@Repository
public class PointOfInterestRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<PointOfInterest> ROW_MAPPER = (rs, rowNum) -> {
        PointOfInterest p = new PointOfInterest();
        p.setId(rs.getInt("poi_id"));
        p.setLocation(rs.getString("location"));
        p.setDescription(rs.getString("description"));
        p.setAttractionId(rs.getInt("attraction_id"));
        return p;
    };

    public PointOfInterestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PointOfInterest save(PointOfInterest poi) {
        String sql = "INSERT INTO points_of_interest (location, description, attraction_id) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, poi.getLocation());
            ps.setString(2, poi.getDescription());
            ps.setInt(3, poi.getAttractionId());
            return ps;
        }, keyHolder);
        Number id = keyHolder.getKey();
        if (id != null)
            poi.setId(id.intValue());
        return poi;
    }

    public List<PointOfInterest> findByAttraction(int attractionId) {
        return jdbcTemplate.query(
                "SELECT * FROM points_of_interest WHERE attraction_id = ?", ROW_MAPPER, attractionId);
    }

    public Optional<PointOfInterest> findById(int id) {
        List<PointOfInterest> result = jdbcTemplate.query(
                "SELECT * FROM points_of_interest WHERE poi_id = ?", ROW_MAPPER, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}