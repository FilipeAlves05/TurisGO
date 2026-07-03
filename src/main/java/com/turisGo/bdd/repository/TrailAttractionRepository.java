package com.turisGo.bdd.repository;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.turisGo.bdd.model.Trail;
import com.turisGo.bdd.model.TouristAttraction;

@Repository
public class TrailAttractionRepository {
    private final JdbcTemplate jdbcTemplate;
    private final TrailRepository trailRepository;
    private final TouristAttractionRepository attractionRepository;

    public TrailAttractionRepository(JdbcTemplate jdbcTemplate, TrailRepository trailRepository,
            TouristAttractionRepository attractionRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.trailRepository = trailRepository;
        this.attractionRepository = attractionRepository;
    }

    public boolean link(int trailId, int attractionId) {
        try {
            jdbcTemplate.update("INSERT INTO trail_attractions (trail_id, attraction_id) VALUES (?, ?)", trailId,
                    attractionId);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    public void unlink(int trailId, int attractionId) {
        jdbcTemplate.update("DELETE FROM trail_attractions WHERE trail_id = ? AND attraction_id = ?", trailId,
                attractionId);
    }

    public List<TouristAttraction> listAttractionsForTrail(int trailId) {
        List<Integer> ids = jdbcTemplate.query("SELECT attraction_id FROM trail_attractions WHERE trail_id = ?",
                (rs, rowNum) -> rs.getInt("attraction_id"), trailId);
        return ids.stream().map(id -> attractionRepository.findById(id).orElse(null)).filter(java.util.Objects::nonNull)
                .toList();
    }

    public List<Trail> listTrailsForAttraction(int attractionId) {
        List<Integer> ids = jdbcTemplate.query("SELECT trail_id FROM trail_attractions WHERE attraction_id = ?",
                (rs, rowNum) -> rs.getInt("trail_id"), attractionId);
        return ids.stream().map(id -> trailRepository.findById(id).orElse(null)).filter(java.util.Objects::nonNull)
                .toList();
    }

}
