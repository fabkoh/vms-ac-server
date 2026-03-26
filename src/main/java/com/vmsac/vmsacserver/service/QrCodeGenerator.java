package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Data
@Service
public class QrCodeGenerator {

    /**
     * Writes the QR image file under {@code ./qrCodes/{qrCodeId}.jpg} where {@code qrCodeId} is the stored
     * opaque token (md5). The <strong>bitmap payload</strong> is {@code scheduledVisitId} as decimal digits so
     * door QR readers report the same string as {@code credUid} on the provisioned Card credential.
     */
    public void setUpQrParams(ScheduledVisit scheduledVisit) throws IOException, WriterException {
        String fileKey = scheduledVisit.getQrCodeId();
        String qrCodeText = String.valueOf(scheduledVisit.getScheduledVisitId());
        String filePath = "./qrCodes/" + fileKey + ".jpg";
        int size = 200;
        String fileType = "jpeg";
        File qrFile = new File(filePath);
        String absFilePath = new File(".").getAbsolutePath();
        //System.out.println(absFilePath);
        createQRImage(qrFile, qrCodeText, size, fileType);

    }
    private void createQRImage(File qrFile, String qrCodeText, int size, String fileType) throws WriterException, IOException {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        ImageIO.write(image, fileType, qrFile);
    }
}
