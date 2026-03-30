package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.model.Visitor;
import com.vmsac.vmsacserver.model.dto.VisitorRegistrationDto;
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

    public void registerVisit(VisitorRegistrationDto dto) throws Exception {
        Visitor incoming = dto.getVisitor();

        Visitor savedVisitor = visitorRepository
                .findByVisitorUid(incoming.getVisitorUid())
                .orElseGet(() -> visitorRepository.save(incoming));

        AccessGroup visitorGroup = accessGroupRepository
                .findByAccessGroupNameAndDeleted(VISITOR_ACCESS_GROUP, false)
                .orElseGet(() -> accessGroupRepository.save(AccessGroup.builder()
                        .accessGroupName(VISITOR_ACCESS_GROUP)
                        .accessGroupDesc("Visitors who self-registered for access")
                        .deleted(false)
                        .isActive(true)
                        .build()));

        ScheduledVisit visit = new ScheduledVisit();
        visit.setVisitor(savedVisitor);
        visit.setQrCodeId(UUID.randomUUID().toString());
        visit.setStartDateOfVisit(dto.getStartDateOfVisit());
        visit.setEndDateOfVisit(dto.getEndDateOfVisit());
        visit.setPurpose(dto.getPurpose());
        visit.setValid(true);
        visit.setOneTimeUse(false);
        ScheduledVisit savedVisit = scheduledVisitRepository.save(visit);

        visitorPassProvisioningService.provisionVisitorPass(savedVisit, savedVisitor, visitorGroup.getAccessGroupId());
        sendQrCodeLink.sendQrCodeLink(savedVisit, savedVisitor);
    }
}
