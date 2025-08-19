package com.itau.registration.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Integer age;

    private String countryCode;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
