package com.gabriel.service.util;

import com.gabriel.domain.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Collection;
import java.util.Properties;

/**
 * Created by liuyufei on 31/10/16.
 */

public class MailSender {

    private final Logger log = LoggerFactory.getLogger(MailSender.class);


    public void sendMail(Collection<Job> jobs) {
        // Recipient's email ID needs to be mentioned.
        String to = "flying3615@163.com";//change accordingly

        // Sender's email ID needs to be mentioned
        String from = "cvhelper2016@gmail.com";//change accordingly

        final String username = "cvhelper2016@gmail.com";//change accordingly
        final String password = "!QAZxsw2#EDC";//change accordingly

        // GMail's SMTP server
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", host);

        // Get the Session object.
        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("New Jobs Published");

            // Now set the actual message
            jobs.forEach(job->{
                    try {
                        message.setText(job.getTitle()+"\n");
                        message.setText(job.getOrigURL()+"\n");
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }

            );

            // Send message
            Transport.send(message);
            log.info("Sent message successfully.... from GMAIL");

        } catch (MessagingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
