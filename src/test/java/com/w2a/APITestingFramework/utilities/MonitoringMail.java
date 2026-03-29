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
	    
	    // 1. Force the correct ClassLoader for Jenkins/Java 21
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    if (classLoader == null) {
	        classLoader = MonitoringMail.class.getClassLoader();
	    }
	    Thread.currentThread().setContextClassLoader(classLoader);

	    Properties props = new Properties();
	    props.put("mail.smtp.host", mailServer);
	    props.put("mail.smtp.port", "465");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.socketFactory.port", "465");
	    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    props.put("mail.smtp.socketFactory.fallback", "false");

	    Authenticator auth = new SMTPAuthenticator();
	    Session session = Session.getInstance(props, auth);

	    try {
	        // 2. Add this manually to prevent the NoClassDefFoundError
	        MimeMessage message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(from));
	        
	        InternetAddress[] addressTo = new InternetAddress[to.length];
	        for (int i = 0; i < to.length; i++) addressTo[i] = new InternetAddress(to[i]);
	        message.setRecipients(Message.RecipientType.TO, addressTo);
	        
	        message.setSubject(subject);
	        
	        // 3. Simple approach to content to avoid DataHandler issues
	        message.setContent(messageBody, "text/html; charset=utf-8");

	        // 4. Update and Send
	        message.saveChanges();
	        Transport.send(message);
	        
	        System.out.println("=== EMAIL SENT SUCCESSFULLY ===");

	    } catch (MessagingException mex) {
	        System.out.println("Mail Error: " + mex.getMessage());
	        mex.printStackTrace();
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