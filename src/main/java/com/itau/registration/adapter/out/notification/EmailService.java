package com.itau.registration.adapter.out.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendRegistrationNotification(Long registrationId, String email) {
        logger.info("Sending email to {} for registration ID {} approval", email, registrationId);
    }
}
