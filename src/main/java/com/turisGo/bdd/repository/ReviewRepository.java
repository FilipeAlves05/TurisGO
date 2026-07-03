// Caminho: bdd/repository/ReviewRepository.java
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

import com.turisGo.bdd.model.Review;

@Repository
public class ReviewRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Review> ROW_MAPPER = (rs, rowNum) -> {
        Review r = new Review();
        r.setId(rs.getInt("review_id"));
        r.setTouristId(rs.getInt("tourist_id"));
        r.setAttractionId(rs.getInt("attraction_id"));
        r.setComment(rs.getString("comment"));
        r.setRating(rs.getObject("rating", Integer.class));
        r.setReviewDate(rs.getDate("review_date") != null ? rs.getDate("review_date").toLocalDate() : null);
        r.setImageUrl(rs.getString("image_url"));
        return r;
    };

    public ReviewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Review upsert(Review review) {
        List<Integer> existing = jdbcTemplate.query(
                "SELECT review_id FROM reviews WHERE tourist_id = ? AND attraction_id = ?",
                (rs, rowNum) -> rs.getInt("review_id"), review.getTouristId(), review.getAttractionId());

        if (review.getReviewDate() == null) {
            review.setReviewDate(LocalDate.now());
        }

        if (!existing.isEmpty()) {
            int reviewId = existing.get(0);
            jdbcTemplate.update(
                    "UPDATE reviews SET comment=?, rating=?, review_date=?, image_url=? WHERE review_id=?",
                    review.getComment(), review.getRating(), Date.valueOf(review.getReviewDate()),
                    review.getImageUrl(), reviewId);
            review.setId(reviewId);
            return review;
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO reviews (tourist_id, attraction_id, comment, rating, review_date, image_url) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, review.getTouristId());
            ps.setInt(2, review.getAttractionId());
            ps.setString(3, review.getComment());
            ps.setInt(4, review.getRating());
            ps.setDate(5, Date.valueOf(review.getReviewDate()));
            ps.setString(6, review.getImageUrl());
            return ps;
        }, keyHolder);

        Number id = keyHolder.getKey();
        if (id != null)
            review.setId(id.intValue());
        return review;
    }

    public List<Review> findByAttraction(int attractionId) {
        return jdbcTemplate.query("SELECT * FROM reviews WHERE attraction_id = ?", ROW_MAPPER, attractionId);
    }

    public List<Review> findByTourist(int touristId) {
        return jdbcTemplate.query("SELECT * FROM reviews WHERE tourist_id = ?", ROW_MAPPER, touristId);
    }

    public Double getAverageRating(int attractionId) {
        return jdbcTemplate.queryForObject(
                "SELECT AVG(rating) FROM reviews WHERE attraction_id = ?", Double.class, attractionId);
    }
}