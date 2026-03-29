package com.w2a.APITestingFramework.utilities;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MonitoringMail {

    public void sendMail(String mailServer, String from, String[] to, String subject, String messageBody) 
            throws MessagingException, AddressException {
        
        Properties props = new Properties();
        
        // SMTP Settings
        props.put("mail.smtp.host", mailServer);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        
        // SSL Settings for Gmail
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        
        // Create Session
        Authenticator auth = new SMTPAuthenticator();
        Session session = Session.getInstance(props, auth);

        try {
            // CRITICAL FIX FOR JAVA 21: Manually set the ClassLoader to handle DataContentHandlers
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            
            // Add recipients
            InternetAddress[] addressTo = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                addressTo[i] = new InternetAddress(to[i]);
            }
            message.setRecipients(Message.RecipientType.TO, addressTo);
            
            message.setSubject(subject);
            message.addHeader("X-Priority", "1");

            // Build Email Content
            MimeMultipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            
            // Set the content as HTML
            messageBodyPart.setContent(messageBody, "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);
            
            message.setContent(multipart);

            // EXTRA FIX: Force update of headers and data handlers before sending
            message.saveChanges(); 

            // Send the email
            Transport.send(message);
            System.out.println("=== EMAIL SENT SUCCESSFULLY TO: " + String.join(", ", to) + " ===");

        } catch (MessagingException mex) {
            System.err.println("=== EMAIL SENDING FAILED ===");
            mex.printStackTrace();
            throw mex;
        }
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            // Uses the static variables from your TestConfig class
            return new PasswordAuthentication(TestConfig.from, TestConfig.password);
        }
    }
}