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
        
        // Basic SMTP Settings
        props.put("mail.smtp.host", mailServer);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        
        // Critical SSL Settings for Gmail Port 465
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        
        // Debugging (Keep this true until you receive your first email)
        props.put("mail.debug", "true");

        // Authenticator using your TestConfig credentials
        Authenticator auth = new SMTPAuthenticator();
        Session session = Session.getInstance(props, auth);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            
            // Build recipient list
            InternetAddress[] addressTo = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                addressTo[i] = new InternetAddress(to[i]);
            }
            message.setRecipients(Message.RecipientType.TO, addressTo);
            
            message.setSubject(subject);
            message.addHeader("X-Priority", "1"); // Marks as High Priority

            // Create the message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(messageBody, "text/html");

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            
            message.setContent(multipart);

            // Execute Send
            Transport.send(message);
            System.out.println("=== EMAIL SENT SUCCESSFULLY ===");

        } catch (MessagingException mex) {
            System.err.println("=== EMAIL SENDING FAILED ===");
            mex.printStackTrace();
            throw mex; // Re-throw so the test suite knows it failed
        }
    }

    // This inner class handles the login handshake
    private class SMTPAuthenticator extends javax.mail.Authenticator {
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            // Pulls the ssathish1992@gmail.com and the 16-digit App Password from TestConfig
            return new PasswordAuthentication(TestConfig.from, TestConfig.password);
        }
    }
}