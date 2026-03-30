package com.vmsac.vmsacserver.controller;

import com.google.zxing.WriterException;
import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.model.dto.ScheduledVisitResponseDto;
import com.vmsac.vmsacserver.repository.ScheduledVisitRepository;
import com.vmsac.vmsacserver.service.QrCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

// TODO: restrict CORS origins
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
// All endpoints require JWT authentication via WebSecurityConfig (/** rule)
public class ScheduledVisitController {

    @Autowired
    private ScheduledVisitRepository scheduledVisitRepository;

    @Autowired
    private QrCodeGenerator qrCodeGenerator;

    @GetMapping("/scheduled-visits")
    List<ScheduledVisitResponseDto> getScheduledVisits() {
        return scheduledVisitRepository.findAll().stream()
                .map(ScheduledVisitResponseDto::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/visit-by-qrcodeid/{qrid}")
    List<ScheduledVisit> getVisitByQrCodeId(@PathVariable("qrid") String qrCodeId) {
        return scheduledVisitRepository.findByQrCodeId(qrCodeId);
    }

    @GetMapping(path = "/qr-code/{qrCodeId}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<Resource> getQrImageFromId(
            @PathVariable("qrCodeId") String qrCodeId) throws IOException, WriterException {
        List<ScheduledVisit> visits = scheduledVisitRepository.findByQrCodeId(qrCodeId);
        if (visits.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ScheduledVisit visit = visits.get(0);
        byte[] qrBytes = qrCodeGenerator.generateQrCode(
                String.valueOf(visit.getScheduledVisitId()), 200, 200);
        ByteArrayResource resource = new ByteArrayResource(qrBytes);
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .body(resource);
    }

    // TODO: Security concern — scheduledVisitId is sequential and guessable.
    // Consider binding this lookup to the authenticated visitor's identity before broadening access.
    @GetMapping(path = "/qr-code/user-request/{scheduledVisitId}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<Resource> getQrImageFromOther(
            @PathVariable("scheduledVisitId") Long scheduledVisitId) throws IOException, WriterException {
        ScheduledVisit visit = scheduledVisitRepository.findById(scheduledVisitId).orElse(null);
        if (visit == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] qrBytes = qrCodeGenerator.generateQrCode(
                String.valueOf(visit.getScheduledVisitId()), 200, 200);
        ByteArrayResource resource = new ByteArrayResource(qrBytes);
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
