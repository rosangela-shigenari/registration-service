package com.itau.registration.adapter.out.notification;

import com.itau.registration.adapter.out.model.RegistrationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class KafkaNotificationPublisher {
    Logger logger = LoggerFactory.getLogger(KafkaNotificationPublisher.class);

    private final KafkaTemplate<String, RegistrationCreatedEvent> kafkaTemplate;
    private static final String TOPIC = "notifications";

    public void publishRegistrationCreatedEvent(Long registrationId, String email, LocalDateTime createdAt) {
        RegistrationCreatedEvent event = new RegistrationCreatedEvent();
        event.setRegistrationId(registrationId);
        event.setEmail(email);
        event.setCreatedAt(createdAt);

        kafkaTemplate.send(TOPIC, event);
        logger.info("Registration Created Event sent to Kafka Topic: {}", TOPIC);
    }
}
