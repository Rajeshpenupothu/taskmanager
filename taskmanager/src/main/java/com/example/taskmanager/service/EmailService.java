package com.example.taskmanager.service;

import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${SENDGRID_API_KEY}")
    private String apiKey;

    // ⚠️ must be the same email you VERIFIED in SendGrid
    private static final String FROM_EMAIL = "tmanager511@gmail.com";

    public void sendResetEmail(String toEmail, String resetLink) {
        Email from = new Email(FROM_EMAIL);
        Email to = new Email(toEmail);

        String subject = "Reset your password";
        Content content = new Content(
                "text/plain",
                "Click the link to reset your password:\n\n" + resetLink
        );

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("SendGrid status: " + response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());

        } catch (IOException ex) {
            throw new RuntimeException("SendGrid email failed", ex);
        }
    }
}