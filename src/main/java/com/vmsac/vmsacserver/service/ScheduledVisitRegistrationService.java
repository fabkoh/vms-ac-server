package com.vmsac.vmsacserver.service;

import com.google.zxing.WriterException;
import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.model.Visitor;
import com.vmsac.vmsacserver.repository.ScheduledVisitRepository;
import com.vmsac.vmsacserver.repository.VisitorRepository;
import com.vmsac.vmsacserver.util.HashQRId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class ScheduledVisitRegistrationService {

    @Autowired
    private ScheduledVisitRepository scheduledVisitRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private HashQRId hashQRId;

    @Autowired
    private QrCodeGenerator qrCodeGenerator;

    @Autowired
    private SendQrCodeLink sendQrCodeLink;

    @Autowired
    private VisitorPassProvisioningService visitorPassProvisioningService;

    /**
     * Persists visit, optional Person+Card credential ({@code credUid = scheduledVisitId}), QR file, SMS.
     */
    @Transactional(rollbackFor = Exception.class)
    public ScheduledVisit register(ScheduledVisit scheduledVisit, Long accessGroupId) throws IOException, WriterException, Exception {
        ScheduledVisit registeredVisit = scheduledVisitRepository.save(scheduledVisit);
        String idStr = Long.toString(registeredVisit.getScheduledVisitId());
        registeredVisit.setQrCodeId(hashQRId.getMd5(idStr));
        registeredVisit = scheduledVisitRepository.save(registeredVisit);

        Visitor visitor = visitorRepository.findByIdNumber(registeredVisit.getVisitorIdNumber());
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor not found for visitor id number: " + registeredVisit.getVisitorIdNumber());
        }
        visitorPassProvisioningService.provisionVisitorPass(registeredVisit, visitor, accessGroupId);

        qrCodeGenerator.setUpQrParams(registeredVisit);
        sendQrCodeLink.sendQrCodeLink(registeredVisit, visitor);
        return registeredVisit;
    }
}
