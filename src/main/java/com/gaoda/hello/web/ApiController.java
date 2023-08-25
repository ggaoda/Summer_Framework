package com.gaoda.hello.web;

import com.gaoda.annotation.Autowired;
import com.gaoda.annotation.GetMapping;
import com.gaoda.annotation.PathVariable;
import com.gaoda.annotation.RestController;
import com.gaoda.exception.DataAccessException;
import com.gaoda.hello.User;
import com.gaoda.hello.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
public class ApiController {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;

    @GetMapping("/api/user/{email}")
    Map<String, Boolean> userExist(@PathVariable("email") String email) {
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        try {
            userService.getUser(email);
            return Map.of("result", Boolean.TRUE);
        } catch (DataAccessException e) {
            return Map.of("result", Boolean.FALSE);
        }
    }

    @GetMapping("/api/users")
    List<User> users() {
        return userService.getUsers();
    }
}