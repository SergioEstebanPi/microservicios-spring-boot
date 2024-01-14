package com.msvc.message.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfig {

    @Value("${rabbitmq.queue.name}")
    private String name;

    @Bean
    public Queue queue(){
        return new Queue(name, true);
    }
}
