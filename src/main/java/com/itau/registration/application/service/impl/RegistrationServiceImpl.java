package com.itau.registration.application.service.impl;

import com.itau.registration.adapter.out.persistence.RegistrationRepository;
import com.itau.registration.adapter.out.notification.KafkaNotificationPublisher;
import com.itau.registration.application.dto.RegistrationRequest;
import com.itau.registration.application.dto.RegistrationResponse;
import com.itau.registration.application.service.RegistrationService;
import com.itau.registration.domain.model.Registration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final KafkaNotificationPublisher notificationPublisher;


    @Override
    public RegistrationResponse createRegistration(RegistrationRequest request) {
        Registration registration = Registration.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .age(request.getAge())
                .countryCode(request.getCountryCode())
                .status("PROCESSING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Registration saved = registrationRepository.save(registration);

        notificationPublisher.publishRegistrationCreatedEvent(saved.getId(), saved.getEmail(), saved.getCreatedAt());

        return mapToResponse(saved, "New registration is processing.");
    }

    @Override
    public Optional<RegistrationResponse> getRegistration(Long id) {
        return registrationRepository.findById(id)
                .map(reg ->
                        mapToResponse(reg, String.format("Registration %d retrieved.", reg.getId())
                ));
    }

    @Override
    public List<RegistrationResponse> getAllRegistrations() {
        return registrationRepository.findAll()
                .stream()
                .map(reg -> mapToResponse(reg, reg.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RegistrationResponse> updateRegistration(Long id, RegistrationRequest request) {
        return registrationRepository.findById(id)
                .map(reg -> {
                    if (request.getFirstName() != null) reg.setFirstName(request.getFirstName());
                    if (request.getLastName() != null) reg.setLastName(request.getLastName());
                    if (request.getEmail() != null) reg.setEmail(request.getEmail());
                    if (request.getAge() != null) reg.setAge(request.getAge());
                    if (request.getCountryCode() != null) reg.setCountryCode(request.getCountryCode());
                    reg.setUpdatedAt(LocalDateTime.now());
                    Registration updated = registrationRepository.save(reg);
                    return mapToResponse(updated, "Registration is updated.");
                });
    }

    @Override
    public boolean deleteRegistration(Long id) {
        return registrationRepository.findById(id)
                .map(reg -> {
                    registrationRepository.delete(reg);
                    return true;
                })
                .orElse(false);
    }

    private RegistrationResponse mapToResponse(Registration registration, String message) {
        return new RegistrationResponse(
                registration.getId(),
                registration.getFirstName(),
                registration.getLastName(),
                registration.getEmail(),
                registration.getAge(),
                registration.getCountryCode(),
                registration.getCreatedAt(),
                registration.getUpdatedAt(),
                message
        );
    }
}
