package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.AccessGroup;
import com.vmsac.vmsacserver.model.Person;
import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.model.Visitor;
import com.vmsac.vmsacserver.model.credential.CreateCredentialDto;
import com.vmsac.vmsacserver.model.credentialtype.CredentialType;
import com.vmsac.vmsacserver.repository.AccessGroupRepository;
import com.vmsac.vmsacserver.repository.CredTypeRepository;
import com.vmsac.vmsacserver.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Creates a {@link Person} + Card {@link com.vmsac.vmsacserver.model.credential.Credential} for a
 * scheduled visit so Pi {@code credOccur} can authorize Wiegand-as-card scans where
 * {@code credUid} matches {@link ScheduledVisit#getScheduledVisitId()} as a string (temporary scheme).
 */
@Service
public class VisitorPassProvisioningService {

    private static final String CARD_TYPE_NAME = "Card";

    @Autowired
    private AccessGroupRepository accessGroupRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CredTypeRepository credTypeRepository;

    @Autowired
    private CredentialService credentialService;

    public void provisionVisitorPass(ScheduledVisit scheduledVisit, Visitor visitor, Long accessGroupId) throws Exception {
        if (accessGroupId == null) {
            return;
        }
        AccessGroup accessGroup = accessGroupRepository
                .findByAccessGroupIdAndDeletedFalse(accessGroupId)
                .orElseThrow(() -> new IllegalArgumentException("Access group not found: " + accessGroupId));

        CredentialType cardType = credTypeRepository
                .findByCredTypeNameAndDeletedFalse(CARD_TYPE_NAME)
                .orElseThrow(() -> new IllegalStateException("Credential type '" + CARD_TYPE_NAME + "' not found"));

        String uid = "VISIT-SV-" + scheduledVisit.getScheduledVisitId();
        if (personRepository.findByPersonUidAndDeleted(uid, false).isPresent()) {
            throw new IllegalStateException("Visitor pass person already exists for scheduled visit: " + scheduledVisit.getScheduledVisitId());
        }

        Person person = Person.builder()
                .personFirstName("Visitor")
                .personLastName(visitor.getLastName() != null && !visitor.getLastName().isBlank()
                        ? visitor.getLastName()
                        : "Pass-" + scheduledVisit.getScheduledVisitId())
                .personUid(uid)
                .personMobileNumber(visitor.getMobileNumber())
                .personEmail(visitor.getEmailAdd())
                .deleted(false)
                .accessGroup(accessGroup)
                .build();
 
        person = personRepository.save(person);

        String credUid = String.valueOf(scheduledVisit.getScheduledVisitId());
        LocalDateTime credTTL = resolveCredTtl(scheduledVisit);

        CreateCredentialDto dto = new CreateCredentialDto();
        dto.setCredUid(credUid);
        dto.setCredTTL(credTTL);
        dto.setValid(true);
        dto.setPerm(false);
        dto.setCredTypeId(cardType.getCredTypeId());
        dto.setPersonId(person.getPersonId());

        credentialService.createCredential(dto);
    }

    private static LocalDateTime resolveCredTtl(ScheduledVisit scheduledVisit) {
        if (scheduledVisit.getEndDateOfVisit() != null) {
            return scheduledVisit.getEndDateOfVisit().atTime(LocalTime.of(23, 59, 59));
        }
        if (scheduledVisit.getStartDateOfVisit() != null) {
            return scheduledVisit.getStartDateOfVisit().atTime(LocalTime.of(23, 59, 59));
        }
        return LocalDateTime.now().plusDays(1);
    }
}
