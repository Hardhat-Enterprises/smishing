package com.example.smishingdetectionapp;

import android.content.Context;
import android.util.Log;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

    private static String generatedVerificationCode; // Store the generated code for debugging/logging

    public static boolean sendVerificationCode(final String recipientEmail, String verificationCode, Context context) {
        final String senderEmail = "manikabhatia2003@gmail.com";
        final String senderPassword = "obji yxhb csqi yinf"; // Use app-specific password

        // Assign the passed verification code to the static variable for debugging/logging purposes
        generatedVerificationCode = verificationCode;

        // Log the generated code for debugging
        Log.d("MailSender", "Generated Verification Code: " + generatedVerificationCode);

        // Configure SMTP properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create and configure the email message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your Verification Code");
            message.setText("Your verification code is: " + generatedVerificationCode);

            // Send the email
            Transport.send(message);

            // Log success
            Log.d("MailSender", "Email sent successfully to: " + recipientEmail);
            return true; // Email sent successfully
        } catch (MessagingException e) {
            // Log failure
            Log.e("MailSender", "Failed to send email: " + e.getMessage(), e);
            return false; // Email sending failed
        }
    }

    // Method to get the last generated verification code (used for debugging)
    public static String getGeneratedVerificationCode() {
        return generatedVerificationCode;
    }
}
