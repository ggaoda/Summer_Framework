package com.itranswarp.scan.destroy;

import com.gaoda.annotation.Bean;
import com.gaoda.annotation.Configuration;
import com.gaoda.annotation.Value;

@Configuration
public class SpecifyDestroyConfiguration {

    @Bean(destroyMethod = "destroy")
    SpecifyDestroyBean createSpecifyDestroyBean(@Value("${app.title}") String appTitle) {
        return new SpecifyDestroyBean(appTitle);
    }
}
