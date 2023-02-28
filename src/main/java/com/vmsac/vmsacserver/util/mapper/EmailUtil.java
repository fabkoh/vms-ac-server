package com.vmsac.vmsacserver.util.mapper;

import com.vmsac.vmsacserver.model.notification.EmailSettings;
import com.vmsac.vmsacserver.repository.EmailSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.net.ssl.SSLContext;


@Service
public class EmailUtil {
    //    /**
//     * Utility method to send simple HTML email
//     * @param session
//     * @param recipient
//     * @param subject
//     * @param body
//     */
//    @Bean
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        // set mailSender properties as needed
//        return mailSender;
//    }
    private static JavaMailSender javaMailSender = null;
    static
    EmailSettingsRepository emailSettingsRepository = null;


    public EmailUtil(JavaMailSender javaMailSender, EmailSettingsRepository emailSettingsRepository) {
        this.javaMailSender = javaMailSender;
        this.emailSettingsRepository = emailSettingsRepository;
    }


    public static void TLSEmail(String recipentEmail, String subject, String text,
                                EmailSettings emailSettings) throws Exception {
        System.out.println("TLSEmail Start");
        final String port = emailSettings.getPortNumber();
        final String host = emailSettings.getHostAddress();
        final String fromEmail = emailSettings.getEmail();
        final String password = emailSettings.getEmailPassword();

        Properties TSLprops = new Properties();

// Setup mail server
        TSLprops.setProperty("mail.smtp.host", host);

// mail username and password
        TSLprops.put("mail.smtp.auth", "true");
        TSLprops.put("mail.smtp.starttls.enable", "true");
        TSLprops.put("mail.debug", "true");
        TSLprops.put("mail.smtp.host", host);
        TSLprops.put("mail.smtp.port", port);
        TSLprops.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session TSLsession = Session.getInstance(TSLprops,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                fromEmail, password);// Specify the Username and the PassWord
                    }
                });

        try {
            sendEmail(TSLsession, recipentEmail, subject, text, fromEmail);
            System.out.println("TLS email sent");

//
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void SSLEmail(String recipentEmail, String subject, String text,
                                EmailSettings emailSettings) throws Exception {

        final String port = emailSettings.getPortNumber();
        final String host = emailSettings.getHostAddress();
        final String fromEmail = emailSettings.getEmail();
        final String password = emailSettings.getEmailPassword();

        System.out.println("SSLEmail Start");

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, null, null);

        SSLContext.setDefault(sslContext);

        String[] enabledCipherSuites = {"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"};

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sslContext.getSocketFactory());
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.ciphersuites", enabledCipherSuites);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

        try {
            sendEmail(session, recipentEmail, subject, text, fromEmail);
            System.out.println("SSL email sent");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    public static void sendEmail(
            Session session, String recipentEmail, String subject, String text, String fromEmail) throws Exception {

        try {
            EmailSettings currentEmailSettings = emailSettingsRepository.findAll().get(0);
            if (!currentEmailSettings.getEnabled()) {
                throw new RuntimeException();
            }
            MimeMessage message = new MimeMessage(session);

            // header field of the header.
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(recipentEmail));
            message.setSubject(subject);
            message.setText(text);

            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            System.out.println("Email sent successfully");

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public static void sendAttachmentEmail(Session session, String toEmail, String subject, String body) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("no_reply@etlas.com", "NoReply-Etlas"));

            msg.setReplyTo(InternetAddress.parse("no_reply@etlas.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(body);

            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Second part is attachment
            messageBodyPart = new MimeBodyPart();
            String filename = "abc.txt";
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            msg.setContent(multipart);

            // Send message
            Transport.send(msg);
            System.out.println("EMail Sent Successfully with attachment!!");
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void sendImageEmail(Session session, String toEmail, String subject, String body) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("no_reply@etlas.com", "NoReply-Etlas"));

            msg.setReplyTo(InternetAddress.parse("no_reply@etlas.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setText(body);

            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Second part is image attachment
            messageBodyPart = new MimeBodyPart();
            String filename = "image.png";
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            //Trick is to add the content-id header here
            messageBodyPart.setHeader("Content-ID", "image_id");
            multipart.addBodyPart(messageBodyPart);

            //third part for displaying image in the email body
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent("<h1>Attached Image</h1>" +
                    "<img src='cid:image_id'>", "text/html");
            multipart.addBodyPart(messageBodyPart);

            //Set the multipart message to the email message
            msg.setContent(multipart);

            // Send message
            Transport.send(msg);
            System.out.println("EMail Sent Successfully with image!!");
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
