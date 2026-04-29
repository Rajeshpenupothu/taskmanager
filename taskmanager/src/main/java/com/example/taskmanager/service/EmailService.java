package com.example.taskmanager.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    private static final String FROM_EMAIL = "tmanager511@gmail.com";

    public void sendResetEmail(String toEmail, String resetLink) {

        Email from = new Email(FROM_EMAIL);
        Email to = new Email(toEmail);

        String subject = "Reset Your Password";

        Content content = new Content(
                "text/plain",
                "Click the link below to reset your password:\n\n" + resetLink
        );

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("EMAIL STATUS: " + response.getStatusCode());

            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("SendGrid error: " + response.getBody());
            }

        } catch (Exception e) {
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }
}