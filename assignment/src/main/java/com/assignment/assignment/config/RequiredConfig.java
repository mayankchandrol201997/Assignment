package com.assignment.assignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class RequiredConfig {
    @Bean
    public ExecutorService getExecutorService()
    {
        return Executors.newFixedThreadPool(10);
    }
}