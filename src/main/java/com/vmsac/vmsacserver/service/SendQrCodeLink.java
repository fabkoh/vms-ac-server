package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.model.Visitor;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpConnectTimeoutException;

@NoArgsConstructor
@Data
@Service
public class SendQrCodeLink {

    public void sendQrCodeLink(ScheduledVisit scheduledVisit, Visitor registeredVisitor) throws IOException, HttpClientErrorException {
        String qrCodeId = scheduledVisit.getQrCodeId();
        String visitorNumber = registeredVisitor.getMobileNumber();
        String message = "Thanks for registering, you may click on the link below to access your QR code entry pass. "+qrCodeId;
        String uri = "http://inthenetworld.com/sms/web_sms.php?apikey=isssecurity&mobileNumber="+visitorNumber+"&smsMessage="+message;

        URL url = new URL(uri);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        int status = con.getResponseCode();

        System.out.println(qrCodeId);
        System.out.println(visitorNumber);
        System.out.println(message);
        System.out.println(uri);
        System.out.println(status);
    }
}
