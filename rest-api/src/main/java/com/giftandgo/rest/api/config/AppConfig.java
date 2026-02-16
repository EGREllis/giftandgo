package com.giftandgo.rest.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.Clock;

@Configuration
@PropertySource("classpath:application.yaml")
public class AppConfig {
    @Bean
    public Clock productionClock() {
        return Clock.systemUTC();
    }
}
