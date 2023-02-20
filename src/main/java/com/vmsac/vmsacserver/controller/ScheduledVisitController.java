package com.vmsac.vmsacserver.controller;

import com.google.zxing.WriterException;
import com.vmsac.vmsacserver.model.Visitor;
import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.repository.ScheduledVisitRepository;
import com.vmsac.vmsacserver.repository.VisitorRepository;
import com.vmsac.vmsacserver.service.QrCodeGenerator;
import com.vmsac.vmsacserver.service.RetrieveQrId;
import com.vmsac.vmsacserver.service.SendQrCodeLink;
import com.vmsac.vmsacserver.util.HashQRId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ScheduledVisitController{

    @Autowired
    private ScheduledVisitRepository scheduledVisitRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private RetrieveQrId retrieveQrId;

    @Autowired
    private QrCodeGenerator qrCodeGenerator;

    @Autowired
    private SendQrCodeLink sendQrCodeLink;

    @Autowired
    private HashQRId hashQRId;

//    @Autowired
//    @Value("${dev.qrcode.image.path}")
//    String qrFilePath;

    public ScheduledVisitController() {

    }

    @GetMapping(path = "/scheduled-visits")
    List<ScheduledVisit> getScheduledVisits(){

        return scheduledVisitRepository.findAll();
    }

    @GetMapping(path = "/visit-by-qrcodeid/{qrid}")
    List<ScheduledVisit> getVisitByQrCodeId(@PathVariable("qrid") String qrCodeId){

        return scheduledVisitRepository.findByQrCodeId(qrCodeId);
    }

    @GetMapping(path = "/qr-code/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    private ResponseEntity<Resource> getQrImageFromId(@PathVariable("id") String qrCodeId) throws IOException {
        Path absFilePath = Paths.get("./qrCodes/"+ qrCodeId + ".jpg");
        //System.out.println("Ab file path:" + absFilePath.toAbsolutePath());
        final ByteArrayResource inputStream = new ByteArrayResource(Files.readAllBytes(absFilePath.toAbsolutePath()));

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStream.contentLength())
                .body(inputStream);

    }


    @GetMapping(path = "/qr-code/user-request/{lastfourdigit}/{startdateofvisit}", produces = MediaType.IMAGE_JPEG_VALUE)
    private ResponseEntity<Resource> getQrImageFromOther(
            @PathVariable("lastfourdigit") String lastFourDigitOfId,
            @PathVariable("startdateofvisit") String startDateOfVisit) throws IOException {

        DateTimeFormatter inputDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate convertStringDateToDate = LocalDate.parse(startDateOfVisit, inputDateFormat);

        String qrCodeId = retrieveQrId.getQrIdFromOther(lastFourDigitOfId, convertStringDateToDate);

        Path absFilePath = Paths.get("./qrCodes/"+ qrCodeId + ".jpg");
        //System.out.println("Ab file path:" + absFilePath.toAbsolutePath());

        final ByteArrayResource inputStream = new ByteArrayResource(Files.readAllBytes(absFilePath.toAbsolutePath()));

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStream.contentLength())
                .body(inputStream);

    }

    @PostMapping(path = "/register-scheduled-visit", consumes = "application/json")
    private ResponseEntity<ScheduledVisit> createScheduledVisit(@Valid @RequestBody ScheduledVisit scheduledVisit) throws URISyntaxException, IOException, WriterException {
        ScheduledVisit registeredVisit = scheduledVisitRepository.save(scheduledVisit);
        String qrCodeId = Long.toString(registeredVisit.getScheduledVisitId());
        registeredVisit.setQrCodeId(hashQRId.getMd5(qrCodeId));
        scheduledVisitRepository.save(registeredVisit);
        qrCodeGenerator.setUpQrParams(registeredVisit);
        Visitor registeredVisitor = visitorRepository.findByIdNumber(registeredVisit.getIdNumber());
        sendQrCodeLink.sendQrCodeLink(registeredVisit, registeredVisitor);
        return ResponseEntity.created(new URI("/api/register-scheduled-visit" + registeredVisit.getScheduledVisitId())).body(registeredVisit);
    }

}
