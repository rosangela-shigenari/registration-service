package com.itau.registration.adapter.out.notification;

import com.itau.registration.adapter.out.model.RegistrationCreatedEvent;
import com.itau.registration.adapter.out.persistence.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class KafkaNotificationConsumer {
    private final RegistrationRepository registrationRepository;
    private final EmailService emailService;

    private static final long DELAY_MILLIS = 2 * 60 * 1000;

    @KafkaListener(topics = "notification", groupId = "notification-group")
    public void consume(RegistrationCreatedEvent event) throws InterruptedException {
        long elapsed = Duration.between(event.getCreatedAt(), LocalDateTime.now()).toMillis();

        if (elapsed < DELAY_MILLIS) {
            Thread.sleep(DELAY_MILLIS - elapsed);
        }

        emailService.sendRegistrationNotification(event.getRegistrationId(), event.getEmail());

        registrationRepository.findById(event.getRegistrationId()).ifPresent(reg -> {
            reg.setStatus("PROCESSED");
            reg.setUpdatedAt(LocalDateTime.now());
            registrationRepository.save(reg);
        });
    }
}
