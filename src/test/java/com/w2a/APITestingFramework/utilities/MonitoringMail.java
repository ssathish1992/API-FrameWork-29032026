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
        
        // Protocol Settings
        props.put("mail.smtp.host", mailServer);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.debug", "true");
        
        // SSL Settings for Port 465
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        // Create Session with Authenticator
        Authenticator auth = new SMTPAuthenticator();
        Session session = Session.getInstance(props, auth);
        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            
            // Set Recipients
            InternetAddress[] addressTo = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                addressTo[i] = new InternetAddress(to[i]);
            }
            message.setRecipients(Message.RecipientType.TO, addressTo);
            
            message.setSubject(subject);
            message.addHeader("X-Priority", "1");

            // Content logic
            BodyPart body = new MimeBodyPart();
            body.setContent(messageBody, "text/html");

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(body);
            message.setContent(multipart);

            // Send
            Transport.send(message);
            System.out.println("Successfully Sent mail to All Users");

        } catch (MessagingException mex) {
            System.out.println("Failed to send mail: " + mex.getMessage());
            mex.printStackTrace();
        }
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            // Ensure these constants exist in your TestConfig class
            String username = TestConfig.from; 
            String password = TestConfig.password; 
            return new PasswordAuthentication(username, password);
        }
    }
}