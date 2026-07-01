package com.turisGo.bdd.repository;

import com.turisGo.bdd.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
        return user;
    };

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", USER_ROW_MAPPER);
    }

    public Optional<User> findById(int id) {
        List<User> result = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        User user = result.get(0);
        user.setContacts(listContacts(id));
        return Optional.of(user);
    }

    public List<String> listContacts(int userId) {
        return jdbcTemplate.query("SELECT contact FROM user_contacts WHERE user_id = ?",
                (rs, rowNum) -> rs.getString("contact"), userId);
    }

    public void addContact(int userId, String contact) {
        jdbcTemplate.update("INSERT INTO user_contacts (user_id, contact) VALUES (?, ?)", userId, contact);
    }

    public void removeContact(int userId, String contact) {
        jdbcTemplate.update("DELETE FROM user_contacts WHERE user_id = ? AND contact = ?", userId, contact);
    }
}
