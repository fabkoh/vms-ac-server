package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.model.Visitor;
import com.vmsac.vmsacserver.model.notification.EmailSettings;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;

@NoArgsConstructor
@Data
@Service
public class SendQrCodeLink {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private QrCodeGenerator qrCodeGenerator;

    public void sendQrCodeLink(ScheduledVisit scheduledVisit, Visitor registeredVisitor) throws Exception {
        String qrCodeId = scheduledVisit.getQrCodeId();
        String message = "Your visit QR code is attached as an image. Please present it at the entrance.";

        String recipientEmail = registeredVisitor.getEmailAdd();
        if (recipientEmail == null || recipientEmail.isBlank()) {
            throw new IllegalArgumentException("Visitor email is required to send the QR link");
        }

        EmailSettings emailSettings = notificationService.getEmailSettings();
        if (!Boolean.TRUE.equals(emailSettings.getEnabled())) {
            throw new IllegalStateException("Email is disabled in EmailSettings");
        }

        byte[] qrImageBytes = qrCodeGenerator.generateQrCode(
                String.valueOf(scheduledVisit.getScheduledVisitId()), 200, 200);
        String attachmentName = "visit-qr-" + scheduledVisit.getScheduledVisitId() + ".jpg";
        String subject = "Your visit QR code";

        File tempQr = File.createTempFile("visit-qr-", ".jpg");
        try {
            Files.write(tempQr.toPath(), qrImageBytes);
            if (Boolean.TRUE.equals(emailSettings.getIsTLS())) {
                notificationService.sendSMTPTLSEmailWithAttachment(
                        message, subject, recipientEmail.trim(), emailSettings, tempQr, attachmentName);
            } else {
                notificationService.sendSMTPSSLEmailWithAttachment(
                        message, subject, recipientEmail.trim(), emailSettings, tempQr, attachmentName);
            }
        } finally {
            tempQr.delete();
        }
    }
}
