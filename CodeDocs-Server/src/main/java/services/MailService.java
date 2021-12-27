package services;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * This class defines functions for sending e-mails
 */
public class MailService {

    /**
     * Function to send an e-mail to specified userEmail
     */
    public static void sendEmail(String recipientEmail, String subject, String messageText) throws IOException {

        // Read the email server details from files
        Properties properties = new Properties();
        FileReader fileReader = new FileReader("CodeDocs-Server/src/main/resources/configurations/mail.properties");
        properties.load(fileReader);

        final String from = properties.getProperty("ADMIN");
        final String host = properties.getProperty("HOST");
        final String port = properties.getProperty("PORT");
        final String username = properties.getProperty("USERNAME");
        final String password = properties.getProperty("PASSWORD");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(messageText);

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}