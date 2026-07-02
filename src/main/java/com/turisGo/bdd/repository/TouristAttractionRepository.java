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

import com.turisGo.bdd.model.TouristAttraction;

@Repository
public class TouristAttractionRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<TouristAttraction> ROW_MAPPER = (rs, rowNum) -> {
        TouristAttraction t = new TouristAttraction();
        t.setId(rs.getInt("attraction_id"));
        t.setName(rs.getString("name"));
        t.setDescription(rs.getString("description"));
        t.setAddress(rs.getString("address"));
        t.setContact(rs.getString("contact"));
        t.setStatus(rs.getString("status"));
        t.setOperatingHours(rs.getString("operating_hours"));
        t.setGallery(rs.getString("gallery"));
        t.setProfileImage(rs.getString("profile_image"));
        t.setInstitutionId(rs.getObject("institution_id", Integer.class));
        return t;
    };

    public TouristAttractionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TouristAttraction save(TouristAttraction t) {
        String sql = "INSERT INTO tourist_attractions " +
                "(name, description, address, contact, status, operating_hours, gallery, profile_image, institution_id) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, t.getName());
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getAddress());
            ps.setString(4, t.getContact());
            ps.setString(5, t.getStatus());
            ps.setString(6, t.getOperatingHours());
            ps.setString(7, t.getGallery());
            ps.setString(8, t.getProfileImage());
            if (t.getInstitutionId() != null)
                ps.setInt(9, t.getInstitutionId());
            else
                ps.setNull(9, java.sql.Types.INTEGER);
            return ps;
        }, keyHolder);
        Number id = keyHolder.getKey();
        if (id != null)
            t.setId(id.intValue());
        return t;
    }

    public void update(TouristAttraction t) {
        String sql = "UPDATE tourist_attractions SET name=?, description=?, address=?, contact=?, " +
                "status=?, operating_hours=?, gallery=?, profile_image=?, institution_id=? WHERE attraction_id=?";
        jdbcTemplate.update(sql, t.getName(), t.getDescription(), t.getAddress(), t.getContact(),
                t.getStatus(), t.getOperatingHours(), t.getGallery(), t.getProfileImage(),
                t.getInstitutionId(), t.getId());
    }

    public List<TouristAttraction> findAll() {
        return jdbcTemplate.query("SELECT * FROM tourist_attractions", ROW_MAPPER);
    }

    public Optional<TouristAttraction> findById(int id) {
        List<TouristAttraction> result = jdbcTemplate.query(
                "SELECT * FROM tourist_attractions WHERE attraction_id = ?", ROW_MAPPER, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<TouristAttraction> findByInstitution(int institutionId) {
        return jdbcTemplate.query(
                "SELECT * FROM tourist_attractions WHERE institution_id = ?", ROW_MAPPER, institutionId);
    }
}