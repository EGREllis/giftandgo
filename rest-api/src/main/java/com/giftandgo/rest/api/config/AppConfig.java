package com.giftandgo.rest.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@PropertySource("classpath:application.yaml")
public class AppConfig {
    @Bean
    public Clock productionClock() {
        return Clock.systemUTC();
    }

    @Bean
    public ExecutorService threadPool() {
        // Arbitrary, tunable.
        return Executors.newFixedThreadPool(8);
    }
}
