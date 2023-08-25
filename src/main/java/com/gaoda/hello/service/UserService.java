package com.gaoda.hello.service;

import com.gaoda.annotation.Autowired;
import com.gaoda.annotation.Component;
import com.gaoda.annotation.Transactional;
import com.gaoda.hello.User;
import com.gaoda.jdbc.JdbcTemplate;

import java.util.List;

@Component
@Transactional
public class UserService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void initDb() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    email VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(50) NOT NULL,
                    password VARCHAR(50) NOT NULL
                )
                """;
        jdbcTemplate.update(sql);
    }

    public User getUser(String email) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email = ?", User.class, email);
    }

    public List<User> getUsers() {
        return jdbcTemplate.queryForList("SELECT email, name FROM users", User.class);
    }

    public User createUser(String email, String name, String password) {
        User user = new User();
        user.email = email.strip().toLowerCase();
        user.name = name.strip();
        user.password = password;
        jdbcTemplate.update("INSERT INTO users (email, name, password) VALUES (?, ?, ?)", user.email, user.name, user.password);
        return user;
    }

}