package com.itau.registration.adapter.out.notification;

import com.itau.registration.adapter.out.model.RegistrationCreatedEvent;
import com.itau.registration.adapter.out.persistence.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class KafkaNotificationConsumer {
    private final RegistrationRepository registrationRepository;
    private final EmailService emailService;

    @Value("${kafka.delay.millis:120000}")
    private long delayMillis;

    @KafkaListener(topics = "notification", groupId = "notification-group")
    public void consume(RegistrationCreatedEvent event) throws InterruptedException {
        long elapsed = Duration.between(event.getCreatedAt(), LocalDateTime.now()).toMillis();

        if (elapsed < delayMillis) {
            Thread.sleep(delayMillis - elapsed);
        }

        emailService.sendRegistrationNotification(event.getRegistrationId(), event.getEmail());

        registrationRepository.findById(event.getRegistrationId()).ifPresent(reg -> {
            reg.setStatus("PROCESSED");
            reg.setUpdatedAt(LocalDateTime.now());
            registrationRepository.save(reg);
        });
    }
}
