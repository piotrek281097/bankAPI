package com.example.demo.services;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService{
    @Override
    public void sendConfirmingTransferEmail(String email, String sendingAccountNumber, String targetAccountNumber, Double money) {

        String to = email;

        String from = "piotrbankapi@gmail.com";
        final String username = "piotrbankapi";
        final String password = "Piotrek2810$";

        String host = "smtp.mailtrap.io";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            message.setSubject("Potwierdzenie przelewu");

            message.setText("Przelew zlecony\nNr nadawcy: " + sendingAccountNumber + "\nNr odbiorcy: " +
                    targetAccountNumber + "\nKwota: " + money);

            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
