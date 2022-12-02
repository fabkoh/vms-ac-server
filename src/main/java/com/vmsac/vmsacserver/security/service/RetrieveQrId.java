package com.vmsac.vmsacserver.security.service;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import com.vmsac.vmsacserver.model.Visitor;
import com.vmsac.vmsacserver.repository.VisitorRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Data
@Service
public class RetrieveQrId {

    @Autowired
    private VisitorRepository visitorRepository;

    public String getQrIdFromOther(String idNumber, LocalDate startDateOfVisit){

        String qrCodeId;
        Visitor visitorScheduledVisits;
        List<ScheduledVisit> visitsByVisitor;

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        visitorScheduledVisits = visitorRepository.findByIdNumber(idNumber);
        visitsByVisitor = visitorScheduledVisits.getVisitorScheduledVisits();
        System.out.println("Date retrieved after conversion:" + startDateOfVisit.format(dateFormat));

        for (ScheduledVisit scheduledVisit : visitsByVisitor){
            System.out.println("Date in for loop:" + scheduledVisit.getStartDateOfVisit());
            System.out.println("Date in for loop after conversion:" + scheduledVisit.getStartDateOfVisit().format(dateFormat));
            System.out.println("Date in for loop variable:" + startDateOfVisit);
            System.out.println("Date in for loop variable after conversion:" + startDateOfVisit.format(dateFormat));
            LocalDate tempDate = scheduledVisit.getStartDateOfVisit();
            if(scheduledVisit.getStartDateOfVisit().format(dateFormat).equals(startDateOfVisit.format(dateFormat))){
                return scheduledVisit.getQrCodeId();
            }
        }
        return null;
    }
}
