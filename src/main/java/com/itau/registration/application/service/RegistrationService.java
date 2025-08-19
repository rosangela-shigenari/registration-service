package com.itau.registration.application.service;


import com.itau.registration.application.dto.RegistrationRequest;
import com.itau.registration.application.dto.RegistrationResponse;

import java.util.List;
import java.util.Optional;

public interface RegistrationService {

    RegistrationResponse createRegistration(RegistrationRequest request);

    Optional<RegistrationResponse> getRegistration(Long id);

    List<RegistrationResponse> getAllRegistrations();

    Optional<RegistrationResponse> updateRegistration(Long id, RegistrationRequest request);

    boolean deleteRegistration(Long id);
}
