package com.itau.registration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class RegistrationApplicationMainTest {

    @Test
    void contextLoads() {
        String[] args = {};
        RegistrationApplication.main(args);
    }
}
