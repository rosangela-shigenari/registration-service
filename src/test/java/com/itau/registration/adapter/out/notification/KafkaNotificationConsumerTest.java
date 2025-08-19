package com.itau.registration.adapter.out.notification;

import com.itau.registration.adapter.out.model.RegistrationCreatedEvent;
import com.itau.registration.adapter.out.persistence.RegistrationRepository;
import com.itau.registration.domain.model.Registration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {"kafka.delay.millis=1"})
class KafkaNotificationConsumerTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private KafkaNotificationConsumer consumer;

    private RegistrationCreatedEvent event;

    @BeforeEach
    void setUp() {
        event = new RegistrationCreatedEvent(
                1L,
                "test@email.com",
                LocalDateTime.now().minusMinutes(5)
        );
    }

    @Test
    @DisplayName("Should send email and update registration when found")
    void testConsumeWithExistingRegistration() throws InterruptedException {
        Registration registration = new Registration();
        registration.setId(1L);
        registration.setStatus("PENDING");
        registration.setUpdatedAt(LocalDateTime.now().minusDays(1));

        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));

        consumer.consume(event);

        verify(emailService, times(1))
                .sendRegistrationNotification(1L, "test@email.com");

        ArgumentCaptor<Registration> captor = ArgumentCaptor.forClass(Registration.class);
        verify(registrationRepository).save(captor.capture());

        Registration saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo("PROCESSED");
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should send email but not update when registration not found")
    void testConsumeWithMissingRegistration() throws InterruptedException {
        when(registrationRepository.findById(1L)).thenReturn(Optional.empty());

        consumer.consume(event);

        verify(emailService, times(1))
                .sendRegistrationNotification(1L, "test@email.com");

        verify(registrationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should wait if event is too recent")
    void testConsumeWithRecentEvent() throws InterruptedException {

        ReflectionTestUtils.setField(consumer, "delayMillis", 3);

        RegistrationCreatedEvent recentEvent = new RegistrationCreatedEvent(
                2L,
                "recent@email.com",
                LocalDateTime.now()
        );

        when(registrationRepository.findById(2L)).thenReturn(Optional.empty());

        long start = System.currentTimeMillis();
        consumer.consume(recentEvent);
        long end = System.currentTimeMillis();

        assertThat(end - start).isGreaterThanOrEqualTo(2);

        verify(emailService).sendRegistrationNotification(2L, "recent@email.com");
    }
}
