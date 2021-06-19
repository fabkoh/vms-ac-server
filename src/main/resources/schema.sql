CREATE TABLE Visitor (
    visitorId BIGINT NOT NULL AUTO_INCREMENT,
    firstName VARCHAR(128) NOT NULL,
    lastName VARCHAR(128) NOT NULL,
    lastFourDigitsOfId VARCHAR(128) NOT NULL,
    mobileNumber VARCHAR(128) NOT NULL,
    emailAdd VARCHAR(128) NOT NULL,
    PRIMARY KEY (visitorId)
)
CREATE TABLE ScheduledVisit (
    scheduledVisitId BIGINT NOT NULL AUTO_INCREMENT,
    visitorId VARCHAR(128) NOT NULL,
    startDateOfVisit VARCHAR(128) NOT NULL,
    endDateOfVisit VARCHAR(128) NOT NULL,
    qrCodeId VARCHAR(128) NOT NULL,
    isValid VARCHAR(128) NOT NULL,
    isOneTimeUse VARCHAR(128) NOT NULL,
    raisedBy VARCHAR(128) NOT NULL,
    PRIMARY KEY (scheduledVisitId)
)
