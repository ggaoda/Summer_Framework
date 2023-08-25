package com.gaoda.hello.service;

import com.gaoda.annotation.Autowired;
import com.gaoda.annotation.Component;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DbInitializer {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;

    @PostConstruct
    void init() {
        logger.info("init database...");
        userService.initDb();
    }
}