package com.itranswarp.scan.init;

import com.gaoda.annotation.Bean;
import com.gaoda.annotation.Configuration;
import com.gaoda.annotation.Value;

@Configuration
public class SpecifyInitConfiguration {

    @Bean(initMethod = "init")
    SpecifyInitBean createSpecifyInitBean(@Value("${app.title}") String appTitle, @Value("${app.version}") String appVersion) {
        return new SpecifyInitBean(appTitle, appVersion);
    }
}