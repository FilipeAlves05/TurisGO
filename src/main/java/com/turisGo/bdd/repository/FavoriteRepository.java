package com.turisGo.bdd.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.turisGo.bdd.model.Trail;
import com.turisGo.bdd.model.TouristAttraction;

@Repository
public class FavoriteRepository {
    private final JdbcTemplate jdbcTemplate;
    private final TrailRepository trailRepository;
    private final TouristAttractionRepository attractionRepository;

    public FavoriteRepository(JdbcTemplate jdbcTemplate, TrailRepository trailRepository,
            TouristAttractionRepository attractionRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.trailRepository = trailRepository;
        this.attractionRepository = attractionRepository;
    }

    public boolean toggleFavoriteTrail(int touristId, int trailId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tourist_favorite_trails WHERE tourist_id = ? AND trail_id = ?", Integer.class,
                touristId, trailId);

        if (count != null && count > 0) {
            jdbcTemplate.update("DELETE FROM tourist_favorite_trails WHERE tourist_id = ? AND trail_id = ?", touristId,
                    trailId);
            return false;
        }

        jdbcTemplate.update("INSERT INTO tourist_favorite_trails(tourist_id, trail_id) VALUES (?, ?)", touristId,
                trailId);

        return true;
    }

    public boolean toggleFavoriteAttraction(int touristId, int attractionId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tourist_favorite_attractions WHERE tourist_id = ? AND attraction_id = ?",
                Integer.class, touristId, attractionId);

        if (count != null && count > 0) {
            jdbcTemplate.update("DELETE FROM tourist_favorite_attractions WHERE tourist_id = ? AND attraction_id = ?",
                    touristId, attractionId);
            return false;
        }

        jdbcTemplate.update("INSERT INTO tourist_favorite_attractions (tourist_id, attraction_id) VALUES (?, ?)",
                touristId, attractionId);
        return true;
    }

    public List<Trail> listFavoriteTrails(int touristId) {
        List<Integer> ids = jdbcTemplate.query("SELECT trail_id FROM tourist_favorite_trails WHERE tourist_id = ?",
                (rs, rowNum) -> rs.getInt("trail_id"), touristId);
        return ids.stream().map(id -> trailRepository.findById(id).orElse(null)).filter(java.util.Objects::nonNull)
                .toList();
    }

    public List<TouristAttraction> listFavoriteAttractions(int touristId) {
        List<Integer> ids = jdbcTemplate.query(
                "SELECT attraction_id FROM tourist_favorite_attractions WHERE tourist_id = ?",
                (rs, rowNum) -> rs.getInt("attraction_id"), touristId);
        return ids.stream().map(id -> attractionRepository.findById(id).orElse(null)).filter(java.util.Objects::nonNull)
                .toList();
    }
}
