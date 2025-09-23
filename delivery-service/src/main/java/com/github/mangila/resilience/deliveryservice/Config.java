package com.github.mangila.resilience.deliveryservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;

@Configuration
public class Config {
    @Bean
    TaskExecutor rabbitListenerExecutor() {
        return new VirtualThreadTaskExecutor("rabbitmq-listener-");
    }
}
