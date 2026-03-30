package com.vmsac.vmsacserver.model.dto;

import com.vmsac.vmsacserver.model.ScheduledVisit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScheduledVisitResponseDto {

    private Long scheduledVisitId;
    private LocalDate visitDate;
    private String purpose;
    private String qrCodeId;
    private boolean valid;

    private String visitorUid;
    private String firstName;
    private String lastName;
    private String emailAdd;
    private String company;

    public static ScheduledVisitResponseDto from(ScheduledVisit visit) {
        ScheduledVisitResponseDto dto = new ScheduledVisitResponseDto();
        dto.scheduledVisitId = visit.getScheduledVisitId();
        dto.visitDate = visit.getVisitDate();
        dto.purpose = visit.getPurpose();
        dto.qrCodeId = visit.getQrCodeId();
        dto.valid = visit.isValid();
        if (visit.getVisitor() != null) {
            dto.visitorUid = visit.getVisitor().getVisitorUid();
            dto.firstName = visit.getVisitor().getFirstName();
            dto.lastName = visit.getVisitor().getLastName();
            dto.emailAdd = visit.getVisitor().getEmailAdd();
            dto.company = visit.getVisitor().getCompany();
        }
        return dto;
    }
}
