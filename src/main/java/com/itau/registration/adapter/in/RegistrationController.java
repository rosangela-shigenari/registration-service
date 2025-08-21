package com.itau.registration.adapter.in;

import com.itau.registration.application.dto.RegistrationRequest;
import com.itau.registration.application.dto.RegistrationResponse;
import com.itau.registration.application.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<RegistrationResponse> createRegistration(@RequestBody @Valid  RegistrationRequest request) {
        RegistrationResponse response = registrationService.createRegistration(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RegistrationResponse>> listRegistrations(
            @RequestParam(required = false) Long id) {

        List<RegistrationResponse> registrationResponseList;

        if (id != null) {
            Optional<RegistrationResponse> registrationResponse = registrationService.getRegistration(id);
            registrationResponseList = registrationResponse
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        } else {
            registrationResponseList = registrationService.getAllRegistrations();
        }

        return registrationResponseList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(registrationResponseList);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<RegistrationResponse> updateRegistration(@PathVariable Long id, @RequestBody RegistrationRequest request) {
        return registrationService.updateRegistration(id, request)
                .map(r -> {
                    r.setMessage("Updated successfully");
                    return ResponseEntity.ok(r);
                })
                .orElseGet(() -> {
                    RegistrationResponse empty = new RegistrationResponse();
                    empty.setMessage("No content");
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(empty);
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RegistrationResponse> deleteRegistration(@PathVariable Long id) {
        RegistrationResponse response = new RegistrationResponse();
        if (registrationService.deleteRegistration(id)) {
            response.setMessage("Deleted successfully");
        } else {
            response.setMessage("No content");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
