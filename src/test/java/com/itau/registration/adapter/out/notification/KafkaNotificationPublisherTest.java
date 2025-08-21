package com.itau.registration.adapter.out.notification;

import com.itau.registration.adapter.out.model.RegistrationCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaNotificationPublisherTest {

    @Mock
    private KafkaTemplate<String, RegistrationCreatedEvent> kafkaTemplate;

    @InjectMocks
    private KafkaNotificationPublisher publisher;

    private Long registrationId;
    private String email;

    @BeforeEach
    void setUp() {
        registrationId = 1L;
        email = "test@email.com";
    }

    @Test
    @DisplayName("Should publish registration created event to Kafka")
    void testPublishRegistrationCreatedEvent() {
        publisher.publishRegistrationCreatedEvent(registrationId, email, LocalDateTime.now());

        ArgumentCaptor<RegistrationCreatedEvent> eventCaptor = ArgumentCaptor.forClass(RegistrationCreatedEvent.class);
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), eventCaptor.capture());

        RegistrationCreatedEvent sentEvent = eventCaptor.getValue();
        String topic = topicCaptor.getValue();

        assertThat(topic).isEqualTo("notifications");
        assertThat(sentEvent.getRegistrationId()).isEqualTo(registrationId);
        assertThat(sentEvent.getEmail()).isEqualTo(email);
    }
}
