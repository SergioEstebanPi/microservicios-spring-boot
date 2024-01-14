package com.msvc.notification;

import com.msvc.notification.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
@EnableDiscoveryClient
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@KafkaListener(topics = "notificationTopic", groupId = "notificationId")
	public void handleNotification(OrderPlacedEvent orderPlacedEvent){
		log.info("Notificacion recibida: {}", orderPlacedEvent.getNumeroPedido());
	}

}
