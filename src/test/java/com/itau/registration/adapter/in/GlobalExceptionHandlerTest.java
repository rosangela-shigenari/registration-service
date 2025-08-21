package com.itau.registration.adapter.in;

import com.itau.registration.application.dto.RegistrationRequest;
import com.itau.registration.application.service.RegistrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegistrationController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @Test
    @DisplayName("Should return 400 and validation error message when fields are missing")
    void testValidationErrorOnCreationWithMissingFields() throws Exception {
        String payload = """
            {
              "firstName": "first",
              "lastName": "last",
              "countryCode": "BR",
              "email": "",
              "age": 19
            }
            """;

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is required"));
    }

    @Test
    @DisplayName("Should return 500 on generic error on delete method")
    void testExceptionHandlingOnGenericError() throws Exception {
        when(registrationService.deleteRegistration(1L))
                .thenThrow(new RuntimeException("Generic error"));

        mockMvc.perform(delete("/registration/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Generic error"));
    }

    @Test
    @DisplayName("Should return 409 on data integrity violation")
    void testExceptionHandlingOnDatabaseDataIntegrityViolation() throws Exception {
        String payload = """
            {
              "firstName": "first",
              "lastName": "last",
              "countryCode": "BR",
              "email": "email@itau.com",
              "age": 19
            }
            """;
        when(registrationService.createRegistration(any()))
                .thenThrow(new DataIntegrityViolationException(""));

        mockMvc.perform(post("/registration")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists please perform an update"));
    }
}

