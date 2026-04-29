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

    // ✅ MUST match verified sender in SendGrid
    private static final String FROM_EMAIL = "tmanager511@gmail.com";
    private static final String FROM_NAME = "Task Manager";

    public void sendResetEmail(String toEmail, String resetLink) {

        System.out.println("🚀 EMAIL SERVICE STARTED");
        System.out.println("📩 To: " + toEmail);

        // 🔒 Safety check
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("❌ SENDGRID_API_KEY is missing!");
        }

        // Optional: print only part of API key for debugging
        System.out.println("🔑 API KEY (partial): " + apiKey.substring(0, 5) + "*****");

        try {
            // ✅ Sender (must be verified in SendGrid)
            Email from = new Email(FROM_EMAIL, FROM_NAME);

            // ✅ Receiver
            Email to = new Email(toEmail);

            String subject = "Reset Your Password";

            // ✅ Use simple content (safe delivery)
            Content content = new Content(
                    "text/plain",
                    "Click the link below to reset your password:\n\n" + resetLink
            );

            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(apiKey);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            System.out.println("📡 Sending email via SendGrid...");

            Response response = sg.api(request);

            System.out.println("✅ SENDGRID STATUS: " + response.getStatusCode());
            System.out.println("📨 SENDGRID RESPONSE: " + response.getBody());

            // ❌ Handle SendGrid errors properly
            if (response.getStatusCode() >= 400) {
                throw new RuntimeException(
                        "SendGrid failed: " + response.getStatusCode() + " - " + response.getBody()
                );
            }

            System.out.println("🎉 Email sent successfully!");

        } catch (Exception e) {
            System.out.println("❌ EMAIL ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }
}