package com.itau.registration.adapter.in;

import com.itau.registration.application.dto.RegistrationRequest;
import com.itau.registration.application.dto.RegistrationResponse;
import com.itau.registration.application.service.RegistrationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private RegistrationController registrationController;

    private RegistrationRequest request;

    @BeforeEach
    void setUp() {
        request = new RegistrationRequest("Rosangela", "Shigenari", "rosangela@email.com", 20, "BR");
    }

    private RegistrationResponse buildResponse(Long id, String firstName, String lastName, String email, int age) {
        return new RegistrationResponse(
                id, firstName, lastName, email, age, "BR",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                "Success"
        );
    }

    // ---------------------- CREATE ----------------------
    @Test
    @DisplayName("Should create a registration successfully")
    void testCreateRegistration() {
        RegistrationResponse response = buildResponse(1L, "Rosangela", "Shigenari", "rosangela@email.com", 20);

        when(registrationService.createRegistration(any())).thenReturn(response);

        ResponseEntity<RegistrationResponse> result = registrationController.createRegistration(request);

        assertThat(result.getStatusCode().value()).isEqualTo(201);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getMessage()).isEqualTo("Success");
    }

    // ---------------------- GET ----------------------
    @Test
    @DisplayName("Should return registration by id when it's found")
    void testGetRegistrationFound() {
        RegistrationResponse response = buildResponse(1L, "Rosangela", "Shigenari", "rosangela@email.com", 28);

        when(registrationService.getRegistration(1L)).thenReturn(Optional.of(response));

        ResponseEntity<List<RegistrationResponse>> result = registrationController.listRegistrations(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().get(0).getMessage()).isEqualTo("Success");
    }

    @Test
    @DisplayName("Should return 204 when registration not found")
    void testGetRegistrationNotFound() {
        when(registrationService.getRegistration(99L)).thenReturn(Optional.empty());

        ResponseEntity<List<RegistrationResponse>> result = registrationController.listRegistrations(99L);

        assertThat(result.getStatusCode().value()).isEqualTo(204);
        assertThat(result.getBody()).isNull();
    }

    // ---------------------- LIST ----------------------
    @Test
    @DisplayName("Should return list of registrations")
    void testListRegistrations() {
        List<RegistrationResponse> responses = Arrays.asList(
                buildResponse(1L, "User1", "Last1", "user1@email.com", 30),
                buildResponse(2L, "User2", "Last2", "user2@email.com", 28)
        );

        when(registrationService.getAllRegistrations()).thenReturn(responses);

        ResponseEntity<List<RegistrationResponse>> result = registrationController.listRegistrations(null);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).hasSize(2);
        assertThat(result.getBody().get(0).getMessage()).isEqualTo("Success");
    }

    @Test
    @DisplayName("Should return 204 when list is empty")
    void testListRegistrationsEmpty() {
        when(registrationService.getAllRegistrations()).thenReturn(List.of());

        ResponseEntity<List<RegistrationResponse>> result = registrationController.listRegistrations(null);

        assertThat(result.getStatusCode().value()).isEqualTo(204);
    }


    // ---------------------- UPDATE ----------------------
    @Test
    @DisplayName("Should update registration successfully")
    void testUpdateRegistrationSuccess() {
        RegistrationResponse response = buildResponse(1L, "Updated", "User", "updated@email.com", 35);

        when(registrationService.updateRegistration(eq(1L), any())).thenReturn(Optional.of(response));

        ResponseEntity<RegistrationResponse> result = registrationController.updateRegistration(1L, request);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        Assertions.assertNotNull(result.getBody());
        assertThat(result.getBody().getMessage()).isEqualTo("Updated successfully");
    }

    @Test
    @DisplayName("Should return 204 when updating non-existent registration")
    void testUpdateRegistrationNotFound() {
        when(registrationService.updateRegistration(eq(99L), any())).thenReturn(Optional.empty());

        ResponseEntity<RegistrationResponse> result = registrationController.updateRegistration(99L, request);

        assertThat(result.getStatusCode().value()).isEqualTo(204);
        Assertions.assertNotNull(result.getBody());
        assertThat(result.getBody().getMessage()).isEqualTo("No content");
    }

    // ---------------------- DELETE ----------------------
    @Test
    @DisplayName("Should delete registration successfully")
    void testDeleteRegistrationSuccess() {
        when(registrationService.deleteRegistration(1L)).thenReturn(true);

        ResponseEntity<RegistrationResponse> result = registrationController.deleteRegistration(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(204);
        Assertions.assertNotNull(result.getBody());
        assertThat(result.getBody().getMessage()).isEqualTo("Deleted successfully");
    }

    @Test
    @DisplayName("Should return 204 when deleting non-existent registration")
    void testDeleteRegistrationNotFound() {
        when(registrationService.deleteRegistration(99L)).thenReturn(false);

        ResponseEntity<RegistrationResponse> result = registrationController.deleteRegistration(99L);

        assertThat(result.getStatusCode().value()).isEqualTo(204);
        Assertions.assertNotNull(result.getBody());
        assertThat(result.getBody().getMessage()).isEqualTo("No content");
    }
}
