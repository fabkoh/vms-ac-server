package com.vmsac.vmsacserver.service;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.model.Visitor;
import com.vmsac.vmsacserver.repository.VisitorRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor
@Data
@Service
public class RetrieveQrId {

    @Autowired
    private VisitorRepository visitorRepository;

    public String getQrIdFromOther(String idNumber, LocalDate startDateOfVisit) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Visitor visitor = visitorRepository.findByVisitorUid(idNumber).orElse(null);
        if (visitor == null) {
            return null;
        }
        List<ScheduledVisit> scheduledVisits = visitor.getScheduledVisits();
        if (scheduledVisits == null) {
            return null;
        }
        for (ScheduledVisit scheduledVisit : scheduledVisits) {
            if (scheduledVisit.getVisitDate().format(dateFormat).equals(startDateOfVisit.format(dateFormat))) {
                return scheduledVisit.getQrCodeId();
            }
        }
        return null;
    }
}
