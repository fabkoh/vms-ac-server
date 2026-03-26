package com.vmsac.vmsacserver.controller;

import com.google.zxing.WriterException;
import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.repository.ScheduledVisitRepository;
import com.vmsac.vmsacserver.service.RetrieveQrId;
import com.vmsac.vmsacserver.service.ScheduledVisitRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RetrieveQrId retrieveQrId;

    @Autowired
    private ScheduledVisitRegistrationService scheduledVisitRegistrationService;

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

    /**
     * Registers a scheduled visit, optional Pi door pass: when {@code accessGroupId} is set, creates a
     * {@link com.vmsac.vmsacserver.model.Person} + Card credential with {@code credUid} = {@link ScheduledVisit#getScheduledVisitId()} as string.
     * Sync {@code credOccur} to controllers after this so the Pi can resolve the credential.
     *
     * @param accessGroupId optional; when present, must reference an existing AccessGroup for the visitor pass
     */
    @PostMapping(path = "/register-scheduled-visit", consumes = "application/json")
    private ResponseEntity<ScheduledVisit> createScheduledVisit(
            @Valid @RequestBody ScheduledVisit scheduledVisit,
            @RequestParam(required = false) Long accessGroupId

    ) throws URISyntaxException, IOException, WriterException, Exception {
        ScheduledVisit registeredVisit = scheduledVisitRegistrationService.register(scheduledVisit, accessGroupId);
        return ResponseEntity.created(new URI("/api/register-scheduled-visit" + registeredVisit.getScheduledVisitId())).body(registeredVisit);
    }

}
