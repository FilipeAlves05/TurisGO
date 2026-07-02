package com.turisGo.bdd.repository;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.turisGo.bdd.model.Achievement;

@Repository
public class TouristAchievementRepository {
    private final JdbcTemplate jdbcTemplate;
    private final AchievementRepository achievementRepository;

    public TouristAchievementRepository(JdbcTemplate jdbcTemplate, AchievementRepository achievementRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.achievementRepository = achievementRepository;
    }

    public boolean grant(int touristId, int achievementId) {
        try {
            jdbcTemplate.update("INSERT INTO tourist_achievements (tourist_id, achievement_id) VALUES (?, ?)",
                    touristId, achievementId);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    public List<Achievement> listByTourist(int touristId) {
        List<Integer> achievementIds = jdbcTemplate.query(
                "SELECT achievement_id FROM tourist_achievements WHERE tourist_id = ?",
                (rs, rowNum) -> rs.getInt("achievement_id"), touristId);
        return achievementIds.stream().map(id -> achievementRepository.findById(id).orElse(null))
                .filter(java.util.Objects::nonNull).toList();
    }
}
