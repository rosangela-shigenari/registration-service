package com.itau.registration.application.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
    private String countryCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Setter
    private String message;
}
