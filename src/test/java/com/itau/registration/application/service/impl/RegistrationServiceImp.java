package com.itau.registration.application.service.impl;

import com.itau.registration.adapter.out.notification.KafkaNotificationPublisher;
import com.itau.registration.adapter.out.persistence.RegistrationRepository;
import com.itau.registration.application.dto.RegistrationRequest;
import com.itau.registration.application.dto.RegistrationResponse;
import com.itau.registration.domain.model.Registration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationServiceImplTest {

    private RegistrationRepository registrationRepository;
    private KafkaNotificationPublisher notificationPublisher;
    private RegistrationServiceImpl registrationService;

    @BeforeEach
    void setUp() {
        registrationRepository = mock(RegistrationRepository.class);
        notificationPublisher = mock(KafkaNotificationPublisher.class);
        registrationService = new RegistrationServiceImpl(registrationRepository, notificationPublisher);
    }

    @Test
    void testCreateRegistration_ShouldSaveAndPublishNotification() {
        RegistrationRequest request = new RegistrationRequest("Rosangela", "Shigenari", "rosangela@gmail.com", 20, "BR");

        Registration saved = Registration.builder()
                .id(1L)
                .firstName("Rosangela")
                .lastName("Shigenari")
                .email("rosangela@gmail.com")
                .age(20)
                .countryCode("BR")
                .status("PROCESSING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(registrationRepository.save(any(Registration.class))).thenReturn(saved);

        RegistrationResponse response = registrationService.createRegistration(request);

        assertNotNull(response);
        assertEquals("Rosangela", response.getFirstName());
        assertEquals("New registration is processing.", response.getMessage());

        verify(registrationRepository, times(1)).save(any(Registration.class));
        verify(notificationPublisher, times(1)).publishRegistrationCreatedEvent(1L, "rosangela@gmail.com", saved.getCreatedAt());
    }

    @Test
    void testGetRegistration_ShouldReturnRegistration_WhenExists() {
        Registration reg = Registration.builder()
                .id(2L)
                .firstName("myFirstName")
                .lastName("myLastName")
                .email("test_123@gmail.com")
                .age(25)
                .countryCode("DE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(registrationRepository.findById(2L)).thenReturn(Optional.of(reg));

        Optional<RegistrationResponse> result = registrationService.getRegistration(2L);

        assertTrue(result.isPresent());
        assertEquals("myFirstName", result.get().getFirstName());
    }

    @Test
    void testGetRegistration_ShouldReturnEmpty_WhenNotExists() {
        when(registrationRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<RegistrationResponse> result = registrationService.getRegistration(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllRegistrations_ShouldReturnList() {
        Registration reg1 = Registration.builder().id(1L).firstName("Rosangela").build();
        Registration reg2 = Registration.builder().id(2L).firstName("Maria").build();

        when(registrationRepository.findAll()).thenReturn(Arrays.asList(reg1, reg2));

        List<RegistrationResponse> result = registrationService.getAllRegistrations();

        assertEquals(2, result.size());
        assertEquals("Rosangela", result.get(0).getFirstName());
    }

    @Test
    void testUpdateRegistration_ShouldUpdateFields() {
        Registration existing = Registration.builder()
                .id(3L)
                .firstName("Old")
                .lastName("")
                .email("old@example.com")
                .age(40)
                .countryCode("BR")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        Registration updated = Registration.builder()
                .id(3L)
                .firstName("New")
                .lastName("")
                .email("new@example.com")
                .age(41)
                .countryCode("US")
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(registrationRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(registrationRepository.save(any(Registration.class))).thenReturn(updated);

        RegistrationRequest request = new RegistrationRequest("New", null, "new@example.com", 41, "US");
        Optional<RegistrationResponse> result = registrationService.updateRegistration(3L, request);

        assertTrue(result.isPresent());
        assertEquals("New", result.get().getFirstName());
        assertEquals("new@example.com", result.get().getEmail());
    }

    @Test
    void shouldNotUpdateFieldsWhenRequestValuesAreNull() {

        Registration existing = new Registration();
        existing.setId(2L);
        existing.setFirstName("KeepFirst");
        existing.setLastName("KeepLast");
        existing.setEmail("keep@email.com");
        existing.setAge(40);
        existing.setCountryCode("CA");

        when(registrationRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(registrationRepository.save(any(Registration.class))).thenAnswer(inv -> inv.getArgument(0));

        RegistrationRequest request = new RegistrationRequest();

        Optional<RegistrationResponse> result = registrationService.updateRegistration(2L, request);

        assertThat(result).isPresent();

        ArgumentCaptor<Registration> captor = ArgumentCaptor.forClass(Registration.class);
        verify(registrationRepository).save(captor.capture());

        Registration saved = captor.getValue();
        assertThat(saved.getFirstName()).isEqualTo("KeepFirst");
        assertThat(saved.getLastName()).isEqualTo("KeepLast");
        assertThat(saved.getEmail()).isEqualTo("keep@email.com");
        assertThat(saved.getAge()).isEqualTo(40);
        assertThat(saved.getCountryCode()).isEqualTo("CA");
    }

    @Test
    void testDeleteRegistration_ShouldReturnTrue_WhenExists() {
        Registration reg = Registration.builder().id(4L).firstName("DeleteItem").build();
        when(registrationRepository.findById(4L)).thenReturn(Optional.of(reg));

        boolean result = registrationService.deleteRegistration(4L);

        assertTrue(result);
        verify(registrationRepository, times(1)).delete(reg);
    }

    @Test
    void testDeleteRegistration_ShouldReturnFalse_WhenNotExists() {
        when(registrationRepository.findById(100L)).thenReturn(Optional.empty());

        boolean result = registrationService.deleteRegistration(100L);

        assertFalse(result);
        verify(registrationRepository, never()).delete(any());
    }
}
