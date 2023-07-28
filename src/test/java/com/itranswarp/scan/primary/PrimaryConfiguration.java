package com.itranswarp.scan.primary;

import com.gaoda.annotation.Bean;
import com.gaoda.annotation.Configuration;
import com.gaoda.annotation.Primary;

@Configuration
public class PrimaryConfiguration {

    @Primary
    @Bean
    DogBean husky() {
        return new DogBean("Husky");
    }

    @Bean
    DogBean teddy() {
        return new DogBean("Teddy");
    }
}