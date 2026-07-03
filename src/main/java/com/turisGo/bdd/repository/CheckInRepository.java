package com.turisGo.bdd.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.turisGo.bdd.model.CheckIn;
import com.turisGo.bdd.model.Tourist;
import com.turisGo.bdd.dto.CheckInRequest;
import com.turisGo.bdd.dto.CheckInResult;

@Repository
public class CheckInRepository {
    private static final int DEFAULT_CHECKIN_POINTS = 10;

    private final JdbcTemplate jdbcTemplate;
    private final TouristRepository touristRepository;
    private final AchievementRepository achievementRepository;
    private TouristAchievementRepository touristAchievementRepository;

    private static final RowMapper<CheckIn> ROW_MAPPER = (rs, rowNum) -> {
        CheckIn checkIn = new CheckIn();
        checkIn.setId(rs.getInt("check_in_id"));
        checkIn.setTouristId(rs.getInt("tourist_id"));
        checkIn.setAttractionId(rs.getInt("attraction_id"));
        checkIn.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
        checkIn.setGeolocation(rs.getString("geolocation"));
        checkIn.setValidatorInstituionId(rs.getObject("validator_institution_id", Integer.class));
        return checkIn;
    };

    public CheckInRepository(JdbcTemplate jdbcTemplate, TouristRepository touristRepository,
            AchievementRepository achievementRepository, TouristAchievementRepository touristAchievementRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.touristRepository = touristRepository;
        this.achievementRepository = achievementRepository;
        this.touristAchievementRepository = touristAchievementRepository;
    }

    @Transactional
    public CheckInResult performCheckIn(CheckInRequest request) {
        Integer alreadyCheckedToday = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM check_ins WHERE tourist_id = ? AND attraction_id = ? AND DATE(date_time) = CURDATE()",
                Integer.class, request.getTouristId(), request.getAttractionId());
        if (alreadyCheckedToday != null && alreadyCheckedToday > 0) {
            throw new IllegalStateException("Já existe um check-in do turista neste ponto turístico hoje!");
        }

        Integer instituionId = jdbcTemplate.queryForObject(
                "SELECT institution_id FROM tourist_attractions WHERE attraction_id = ?", Integer.class,
                request.getAttractionId());

        LocalDateTime now = LocalDateTime.now();

        KeyHolder checkInKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO check_ins (geolocation, date_time, tourist_id, attraction_id, validator_institution_id) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.getGeolocation());
            ps.setTimestamp(2, Timestamp.valueOf(now));
            ps.setInt(3, request.getTouristId());
            ps.setInt(4, request.getAttractionId());
            if (instituionId != null) {
                ps.setInt(5, instituionId);
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            return ps;
        }, checkInKeyHolder);

        Number checkInIdNum = checkInKeyHolder.getKey();
        int checkInId = checkInIdNum.intValue();

        int amount = request.getAmount() != null ? request.getAmount() : DEFAULT_CHECKIN_POINTS;

        jdbcTemplate.update(
                "INSERT INTO points_history(amount, description, event_date, check_in_id) VALUES (?, ?, ?, ?)", amount,
                "Pontos por check-in", Timestamp.valueOf(now), checkInId);

        touristRepository.addPoints(request.getTouristId(), amount);

        checkAndGrantMilestoneAchievements(request.getTouristId());

        CheckIn checkIn = new CheckIn();
        checkIn.setId(checkInId);
        checkIn.setGeolocation(request.getGeolocation());
        checkIn.setDateTime(now);
        checkIn.setTouristId(request.getTouristId());
        checkIn.setAttractionId(request.getAttractionId());
        checkIn.setValidatorInstituionId(instituionId);

        Tourist updatedTourist = touristRepository.findById(request.getTouristId()).orElseThrow();

        return new CheckInResult(checkIn, amount, updatedTourist.getTotalPoints(), updatedTourist.getLevel());
    }

    public void checkAndGrantMilestoneAchievements(int touristId) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM check_ins WHERE tourist_id = ?",
                Integer.class, touristId);

        if (count == null) {
            return;
        }

        if (count == 1) {
            grantIfExists(touristId, "Primeiro check-in");
        } else if (count == 10) {
            grantIfExists(touristId, "Explorador");
        } else if (count == 50) {
            grantIfExists(touristId, "Aventureiro");
        }
    }

    private void grantIfExists(int touristId, String achievementName) {
        achievementRepository.findByName(achievementName)
                .ifPresent(a -> touristAchievementRepository.grant(touristId, a.getId()));
    }

    public List<CheckIn> findByTourist(int touristId) {
        return jdbcTemplate.query("SELECT * FROM check_ins WHERE tourist_id = ?", ROW_MAPPER, touristId);
    }

    public List<CheckIn> findByAttraction(int attractionId) {
        return jdbcTemplate.query("SELECT * FROM check_ins WHERE attraction_id = ?", ROW_MAPPER, attractionId);
    }

    public List<CheckIn> findAll() {
        return jdbcTemplate.query("SELECT * FROM check_ins", ROW_MAPPER);
    }

    public Optional<CheckIn> findById(int id) {
        List<CheckIn> result = jdbcTemplate.query("SELECT * FROM check_ins WHERE check_in_id = ?", ROW_MAPPER, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}
