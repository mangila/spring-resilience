package com.github.mangila.resilience.deliveryservice;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;

@Configuration
@Slf4j
public class Config {

    @Bean
    TaskExecutor rabbitVirtualThreadTaskExecutor() {
        return new VirtualThreadTaskExecutor("rabbitmq-listener-");
    }

    @Bean
    RabbitListenerErrorHandler rabbitListenerErrorHandler() {
        return new RabbitListenerErrorHandler() {
            @Override
            public Object handleError(Message amqpMessage,
                                      Channel channel,
                                      org.springframework.messaging.Message<?> message,
                                      ListenerExecutionFailedException exception) throws Exception {
                log.error("RabbitMQ listener error: {}", exception.getMessage(), exception);
                return null;
            }
        };
    }
}
