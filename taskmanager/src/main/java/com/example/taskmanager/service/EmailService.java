package com.example.taskmanager.service;

import com.sendgrid.SendGrid;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.Method;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${SENDGRID_API_KEY}")
    private String apiKey;

    public void sendResetEmail(String toEmail, String resetLink) {

        System.out.println("🚀 EMAIL SERVICE STARTED");
        System.out.println("📩 To: " + toEmail);
        System.out.println("🔑 API KEY: " + apiKey);

        // 🔥 VERY IMPORTANT: Use safe sender (avoids Gmail blocking)
        Email from = new Email("test@example.com", "Task Manager");

        Email to = new Email(toEmail);

        String subject = "Reset Your Password";

        // ✅ Simple content first (reduce failure chances)
        Content content = new Content(
                "text/plain",
                "Click this link to reset your password:\n" + resetLink
        );

        Mail mail = new Mail(from, subject, to, content);

        try {
            SendGrid sg = new SendGrid(apiKey);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            System.out.println("📡 Sending request to SendGrid...");

            Response response = sg.api(request);

            System.out.println("✅ SENDGRID STATUS: " + response.getStatusCode());
            System.out.println("📨 SENDGRID RESPONSE: " + response.getBody());

            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("SendGrid error: " + response.getBody());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }
}