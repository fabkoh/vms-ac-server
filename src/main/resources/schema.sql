CREATE TABLE Visitor (
    visitorId BIGINT NOT NULL AUTO_INCREMENT,
    firstName VARCHAR(128),
    lastName VARCHAR(128),
    lastFourDigitsOfId VARCHAR(128),
    mobileNumber VARCHAR(128),
    emailAdd VARCHAR(128),
    PRIMARY KEY (visitorId)
);
CREATE TABLE ScheduledVisit (
    scheduledVisitId BIGINT NOT NULL AUTO_INCREMENT,
    visitorId BIGINT,
    startDateOfVisit DATE,
    endDateOfVisit DATE,
    qrCodeId VARCHAR(128),
    valid BOOLEAN,
    oneTimeUse BOOLEAN,
    raisedBy BIGINT,
    PRIMARY KEY (scheduledVisitId),
    FOREIGN KEY (visitorId) REFERENCES Visitor (visitorId)
);
