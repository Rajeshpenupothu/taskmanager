import com.sendgrid.SendGrid;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.Method;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.TrackingSettings;
import com.sendgrid.helpers.mail.objects.ClickTracking;

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

        try {
            // ✅ Sender
            Email from = new Email(FROM_EMAIL, FROM_NAME);

            // ✅ Receiver
            Email to = new Email(toEmail);

            String subject = "Task Manager - Reset Your Password";

            // ✅ Clean HTML Email (professional look)
            Content content = new Content(
                    "text/html",
                    "<div style='font-family:Arial,sans-serif; padding:20px;'>"
                    + "<h2 style='color:#333;'>Reset Your Password</h2>"
                    + "<p>Hello,</p>"
                    + "<p>Click the button below to reset your password:</p>"

                    + "<a href='" + resetLink + "' "
                    + "style='display:inline-block;padding:12px 20px;"
                    + "background-color:#4CAF50;color:white;text-decoration:none;"
                    + "border-radius:5px;font-weight:bold;'>"
                    + "Reset Password</a>"

                    + "<p style='margin-top:20px;'>This link will expire in 10 minutes.</p>"
                    + "<p>If you didn’t request this, ignore this email.</p>"

                    + "<br><p style='color:#888;'>Task Manager Team</p>"
                    + "</div>"
            );

            Mail mail = new Mail(from, subject, to, content);

            // ✅ IMPORTANT: Disable SendGrid tracking (removes ugly long links)
            TrackingSettings trackingSettings = new TrackingSettings();
            trackingSettings.setClickTracking(new ClickTracking(false, false));
            mail.setTrackingSettings(trackingSettings);

            SendGrid sg = new SendGrid(apiKey);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            System.out.println("📡 Sending email via SendGrid...");

            Response response = sg.api(request);

            System.out.println("✅ SENDGRID STATUS: " + response.getStatusCode());
            System.out.println("📨 SENDGRID RESPONSE: " + response.getBody());

            // ❌ Handle errors
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