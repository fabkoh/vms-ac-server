package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.ScheduledVisit;

import com.vmsac.vmsacserver.model.Visitor;
import com.vmsac.vmsacserver.model.dto.ScheduleVisitDto;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
import com.vmsac.vmsacserver.repository.ScheduledVisitRepository;
import com.vmsac.vmsacserver.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class VisitorService {

    private static final String VISITOR_ACCESS_GROUP = "Visitor Access Group";

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private ScheduledVisitRepository scheduledVisitRepository;

    @Autowired
    private AccessGroupRepository accessGroupRepository;

    @Autowired
    private VisitorPassProvisioningService visitorPassProvisioningService;

    @Autowired
    private SendQrCodeLink sendQrCodeLink;

    public Optional<Visitor> findByVisitorUid(String visitorUid) {
        return visitorRepository.findByVisitorUid(visitorUid);
    }

    public Visitor registerVisitor(Visitor visitor) {
        return visitorRepository.findByVisitorUid(visitor.getVisitorUid())
                .orElseGet(() -> visitorRepository.save(visitor));
    }

    public void scheduleVisit(ScheduleVisitDto dto) throws Exception {
        Visitor visitor = visitorRepository.findByVisitorUid(dto.getVisitorUid())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Visitor not found for UID: " + dto.getVisitorUid()));

        AccessGroup visitorGroup = accessGroupRepository
                .findByAccessGroupNameAndDeleted(VISITOR_ACCESS_GROUP, false)
                .orElseThrow(() -> new IllegalStateException("Visitor Access Group not found — ensure it is seeded in data.sql"));

        ScheduledVisit visit = new ScheduledVisit();
        visit.setVisitor(visitor);
        visit.setQrCodeId(UUID.randomUUID().toString());
        visit.setVisitDate(dto.getVisitDate());
        visit.setPurpose(dto.getPurpose());
        visit.setValid(true);
        visit.setOneTimeUse(false);
        ScheduledVisit savedVisit = scheduledVisitRepository.save(visit);

        visitorPassProvisioningService.provisionVisitorPass(savedVisit, visitor, visitorGroup.getAccessGroupId());

        try {
            sendQrCodeLink.sendQrCodeLink(savedVisit, visitor);
        } catch (Exception e) {
            // Email failure should not roll back a valid visit registration
            System.err.println("Failed to send QR code email: " + e.getMessage());
        }
    }
}
