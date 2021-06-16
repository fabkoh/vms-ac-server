CREATE TABLE Visitor (
    visitorId BIGINT NOT NULL AUTO_INCREMENT,
    firstName VARCHAR(128) NOT NULL,
    lastName VARCHAR(128) NOT NULL,
    lastFourDigitsOfId VARCHAR(128) NOT NULL,
    mobileNumber VARCHAR(128) NOT NULL,
    emailAdd VARCHAR(128) NOT NULL,
    PRIMARY KEY (visitorId)
);