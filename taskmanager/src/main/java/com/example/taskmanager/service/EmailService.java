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

        // ✅ FIX: Use SendGrid trusted sender (NOT gmail)
        Email from = new Email("tmanager511@gmail.com", "Task Manager");

        Email to = new Email(toEmail);

        String subject = "Reset Your Password";

        // ✅ Better deliverability with HTML
        Content content = new Content(
                "text/html",
                "<h3>Password Reset</h3>" +
                "<p>Click the button below to reset your password:</p>" +
                "<a href='" + resetLink + "' " +
                "style='padding:10px 15px;background:#4CAF50;color:white;text-decoration:none;'>Reset Password</a>"
        );

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            // ✅ Debug logs
            System.out.println("EMAIL STATUS: " + response.getStatusCode());
            System.out.println("RESPONSE BODY: " + response.getBody());

            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("SendGrid error: " + response.getBody());
            }

        } catch (Exception e) {
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }
}