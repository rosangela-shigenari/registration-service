package com.itau.registration.adapter.in;

import com.itau.registration.application.dto.RegistrationRequest;
import com.itau.registration.application.dto.RegistrationResponse;
import com.itau.registration.application.service.RegistrationService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<RegistrationResponse> createRegistration(@RequestBody @Valid  RegistrationRequest request) {
        RegistrationResponse response = registrationService.createRegistration(request);
        response.setMessage("Created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationResponse> getRegistration(@PathVariable Long id) {
        return registrationService.getRegistration(id)
                .map(r -> {
                    r.setMessage("Success");
                    return ResponseEntity.ok(r);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<RegistrationResponse>> listAllRegistrations() {
        List<RegistrationResponse> list = registrationService.getAllRegistrations();
        if (list.isEmpty()) {
            RegistrationResponse empty = new RegistrationResponse();
            empty.setMessage("No registration found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(List.of(empty));
        }
        return ResponseEntity.ok(list);
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
