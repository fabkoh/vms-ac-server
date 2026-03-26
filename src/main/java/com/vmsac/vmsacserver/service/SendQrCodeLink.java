package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.model.Visitor;
import com.vmsac.vmsacserver.model.notification.EmailSettings;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor
@Data
@Service
public class SendQrCodeLink {

    @Value("${vms.visitor.qrcode-page-url}")
    private String visitorQrcodePageUrl;

    @Autowired
    private NotificationService notificationService;

    public void sendQrCodeLink(ScheduledVisit scheduledVisit, Visitor registeredVisitor) throws Exception {
        String qrCodeId = scheduledVisit.getQrCodeId();
        String link =
                visitorQrcodePageUrl
                + (visitorQrcodePageUrl.contains("?") ? "&" : "?")
                + "qrCode="
                + URLEncoder.encode(qrCodeId, StandardCharsets.UTF_8);
        String message =
                "Your visit QR code is attached as an image.\n\n"
                + "You can also open this link in a browser:\n"
                + link;

        /*
        // SMS (disabled for now — use email only)
        String visitorNumber = registeredVisitor.getMobileNumber();
        String urlAddress = "http://inthenetworld.com/sms/send_sms.php";
        URL url = new URL(urlAddress);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        OutputStream opstream = httpURLConnection.getOutputStream();
        BufferedWriter bfwriter = new BufferedWriter(new OutputStreamWriter(opstream, "UTF-8"));
        String dstring = URLEncoder.encode("apikey", "UTF-8") + "="
                + URLEncoder.encode("isssecurity", "UTF-8") + "&"
                + URLEncoder.encode("mobileNumber", "UTF-8") + "="
                + URLEncoder.encode(visitorNumber, "UTF-8") + "&"
                + URLEncoder.encode("smsMessage", "UTF-8") + "="
                + URLEncoder.encode(message, "UTF-8");
        bfwriter.write(dstring);
        bfwriter.flush();
        bfwriter.close();
        opstream.close();
        InputStream ipstream = httpURLConnection.getInputStream();
        ipstream.close();
        httpURLConnection.disconnect();
        */

        String recipientEmail = registeredVisitor.getEmailAdd();
        if (recipientEmail == null || recipientEmail.isBlank()) {
            throw new IllegalArgumentException("Visitor email is required to send the QR link");
        }

        EmailSettings emailSettings = notificationService.getEmailSettings();
        if (!Boolean.TRUE.equals(emailSettings.getEnabled())) {
            throw new IllegalStateException("Email is disabled in EmailSettings");
        }

        File qrImage = new File("./qrCodes/" + qrCodeId + ".jpg");
        if (!qrImage.isFile()) {
            throw new IllegalStateException("QR image not found at " + qrImage.getAbsolutePath());
        }
        String attachmentName = "visit-qr-" + scheduledVisit.getScheduledVisitId() + ".jpg";

        String subject = "Your visit QR code";
        if (Boolean.TRUE.equals(emailSettings.getIsTLS())) {
            notificationService.sendSMTPTLSEmailWithAttachment(
                    message, subject, recipientEmail.trim(), emailSettings, qrImage, attachmentName);
        } else {
            notificationService.sendSMTPSSLEmailWithAttachment(
                    message, subject, recipientEmail.trim(), emailSettings, qrImage, attachmentName);
        }

        System.out.println(qrCodeId);
        System.out.println(recipientEmail);
        System.out.println(message);
    }
}
