package com.vmsac.vmsacserver.util.mapper;

// Interface
public interface EmailService {

    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetails details);

    // Method
    // To send an email with attachment
//    String sendMailWithAttachment(EmailDetails details);
}
