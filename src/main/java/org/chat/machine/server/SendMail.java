package org.chat.machine.server;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendMail {
    private static Session session;

    public static void main() {
        session = CreateSession();
    }

    public static void send(String title, String text, String to) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setSubject(title);
        message.setText(text);
        message.setFrom(new InternetAddress(ConfigOperation.ReadConfig("mail.from")));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
        Transport.send(message);
    }

    private static Session CreateSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", ConfigOperation.ReadConfig("mail.smtp").split(":")[0]);
        props.put("mail.smtp.port", ConfigOperation.ReadConfig("mail.smtp").split(":")[1]);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enale", "true");

        return Session.getInstance(props, new Authenticator() {
            @Override
            public javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(
                        ConfigOperation.ReadConfig("mail.from"),
                        ConfigOperation.ReadConfig("mail.password")
                );
            }
        });
    }
}
