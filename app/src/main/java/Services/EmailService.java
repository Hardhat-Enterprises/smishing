package Services;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public abstract class EmailService {

    private static final String SMTP_HOST = System.getenv("SMTP_HOST");
    private static final String SMTP_PORT = System.getenv("SMTP_PORT");
    private static final String EMAIL_USER = System.getenv("EMAIL_USER");
    private static final String EMAIL_PASSWORD = System.getenv("EMAIL_PASSWORD");

    public static void send(String to, String subject, String body) {
        // Set up email server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create a session with an authenticator
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USER, EMAIL_PASSWORD);
            }
        });

        try {
            // Create an email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendLockoutNotification(String userEmail) {
        String subject = "Account Locked";
        String body = "Your account has been locked due to multiple failed login attempts. Please contact support if you believe this is an error.";
        send(userEmail, subject, body);
    }}




