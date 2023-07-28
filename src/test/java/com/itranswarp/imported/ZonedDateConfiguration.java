package com.itranswarp.imported;

import com.gaoda.annotation.Bean;
import com.gaoda.annotation.Configuration;

import java.time.ZonedDateTime;

@Configuration
public class ZonedDateConfiguration {

    @Bean
    ZonedDateTime startZonedDateTime() {
        return ZonedDateTime.now();
    }
}