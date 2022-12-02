package com.vmsac.vmsacserver.security.service;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.model.Visitor;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpConnectTimeoutException;

@NoArgsConstructor
@Data
@Service
public class SendQrCodeLink {

    public void sendQrCodeLink(ScheduledVisit scheduledVisit, Visitor registeredVisitor) throws IOException, HttpClientErrorException {
        String qrCodeId = scheduledVisit.getQrCodeId();
        String visitorNumber = registeredVisitor.getMobileNumber();
        String message = "Please click on link for QR code: http://ec2-13-212-193-249.ap-southeast-1.compute.amazonaws.com:5000/visitor/qrcode?qrCode="+qrCodeId;

        String urlAddress = "http://inthenetworld.com/sms/send_sms.php";

        URL url = new URL(urlAddress);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        OutputStream opstream = httpURLConnection.getOutputStream();
        BufferedWriter bfwriter = new BufferedWriter(new OutputStreamWriter(opstream, "UTF-8"));

        String dstring = URLEncoder.encode("apikey", "UTF-8")+"="+
                URLEncoder.encode("isssecurity", "UTF-8")+"&"+
                URLEncoder.encode("mobileNumber", "UTF-8")+"="+
                URLEncoder.encode(visitorNumber, "UTF-8") +"&"+
                URLEncoder.encode("smsMessage", "UTF-8")+"="+
                URLEncoder.encode(message, "UTF-8");

        bfwriter.write(dstring);
        bfwriter.flush();
        bfwriter.close();
        opstream.close();
        InputStream ipstream = httpURLConnection.getInputStream();
        ipstream.close();
        httpURLConnection.disconnect();

        System.out.println(qrCodeId);
        System.out.println(visitorNumber);
        System.out.println(message);
        System.out.println(urlAddress);
        //System.out.println(status);
    }
}
