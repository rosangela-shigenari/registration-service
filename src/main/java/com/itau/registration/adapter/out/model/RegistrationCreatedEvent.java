package com.itau.registration.adapter.out.model;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationCreatedEvent {
    private Long registrationId;
    private String email;
    private LocalDateTime createdAt;
}
