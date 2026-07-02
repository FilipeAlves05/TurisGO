package com.turisGo.bdd.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.turisGo.bdd.model.Itinerary;

@Repository
public class ItineraryRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Itinerary> ROW_MAPPER = (rs, rowNum) -> {
        Itinerary i = new Itinerary();
        i.setId(rs.getInt("itinerary_id"));
        i.setName(rs.getString("name"));
        i.setStartDate(rs.getDate("start_date") != null ? rs.getDate("start_date").toLocalDate() : null);
        i.setEndDate(rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null);
        i.setTouristId(rs.getInt("tourist_id"));
        return i;
    };

    public ItineraryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Itinerary save(Itinerary itinerary) {
        String sql = "INSERT INTO itineraries (name, start_date, end_date, tourist_id) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, itinerary.getName());
            ps.setDate(2, itinerary.getStartDate() != null ? Date.valueOf(itinerary.getStartDate()) : null);
            ps.setDate(3, itinerary.getEndDate() != null ? Date.valueOf(itinerary.getEndDate()) : null);
            ps.setInt(4, itinerary.getTouristId());
            return ps;
        }, keyHolder);
        Number id = keyHolder.getKey();
        if (id != null)
            itinerary.setId(id.intValue());
        return itinerary;
    }

    public List<Itinerary> findAll() {
        return jdbcTemplate.query("SELECT * FROM itineraries", ROW_MAPPER);
    }

    public Optional<Itinerary> findById(int id) {
        List<Itinerary> result = jdbcTemplate.query(
                "SELECT * FROM itineraries WHERE itinerary_id = ?", ROW_MAPPER, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<Itinerary> findByTourist(int touristId) {
        return jdbcTemplate.query("SELECT * FROM itineraries WHERE tourist_id = ?", ROW_MAPPER, touristId);
    }
}