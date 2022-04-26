CREATE TABLE IF NOT EXISTS Visitor (
    idNumber VARCHAR(128) NOT NULL,
    firstName VARCHAR(128),
    lastName VARCHAR(128),
    mobileNumber VARCHAR(128),
    emailAdd VARCHAR(128),
    PRIMARY KEY (idNumber)
);

CREATE TABLE IF NOT EXISTS ScheduledVisit (
    scheduledVisitId SERIAL NOT NULL,
    idNumber VARCHAR(128) REFERENCES Visitor (idNumber),
    startDateOfVisit DATE,
    endDateOfVisit DATE,
    qrCodeId VARCHAR(128),
    valid BOOLEAN,
    oneTimeUse BOOLEAN,
    raisedBy BIGINT,
    PRIMARY KEY (scheduledVisitId)
);

CREATE TABLE IF NOT EXISTS AccessGroups (
  accessGroupId SERIAL NOT NULL UNIQUE,
  accessGroupName VARCHAR(255) NOT NULL,
  accessGroupDesc TEXT,
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (accessGroupId)
);

CREATE TABLE IF NOT EXISTS Persons (
  personId SERIAL NOT NULL UNIQUE,
  personFirstName VARCHAR(128) NOT NULL,
  personLastName VARCHAR(128) NOT NULL,
  personUID VARCHAR(128) NOT NULL,
  personMobileNumber VARCHAR(128),
  personEmail VARCHAR(128),
  accessGroupId INT REFERENCES AccessGroups (accessGroupId),
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (personId)
);

CREATE TABLE IF NOT EXISTS OrgGroups (
  orgGroupId SERIAL NOT NULL UNIQUE,
  orgGroupName VARCHAR(255) NOT NULL,
  orgGroupDesc TEXT,
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (orgGroupId)
);

CREATE TABLE IF NOT EXISTS PersonOrgGroupNtoN (
  personOrgGroupId SERIAL NOT NULL UNIQUE,
  personId INT REFERENCES Persons (personId),
  orgGroupId INT REFERENCES OrgGroups (orgGroupId),
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (personOrgGroupId)
);

CREATE TABLE IF NOT EXISTS CredentialType(
  credTypeId SERIAL NOT NULL UNIQUE,
  credTypeName VARCHAR(255) NOT NULL UNIQUE,
  credTypeDesc VARCHAR(255),
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (credTypeId)
);

CREATE TABLE IF NOT EXISTS Credentials (
  credId SERIAL NOT NULL UNIQUE,
  credUID VARCHAR(255) NOT NULL,
  credTTL TIMESTAMP NOT NULL,
  isValid BOOLEAN NOT NULL,
  isPrem BOOLEAN NOT NULL,
  credTypeId INT REFERENCES CredentialType (credTypeId),
  personId INT REFERENCES Persons (personId),
  scheduledVisitId INT References ScheduledVisit (scheduledVisitId),
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (credId)
);

CREATE TABLE IF NOT EXISTS Entrances(
  entranceId SERIAL NOT NULL UNIQUE,
  entranceName VARCHAR(255) NOT NULL,
  entranceDesc VARCHAR(255),
  isActive BOOLEAN NOT NULL,
  deleted BOOLEAN NOT NULL,
  used BOOLEAN NOT NULL,
  PRIMARY KEY (entranceId)
);

CREATE TABLE IF NOT EXISTS AccessGroupsEntranceNtoN(
  groupToEntranceId SERIAL NOT NULL UNIQUE,
  accessGroupId INT REFERENCES AccessGroups (accessGroupId),
  entranceId INT REFERENCES Entrances (entranceId),
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (groupToEntranceId)
);

CREATE TABLE IF NOT EXISTS AccessGroupSchedule(
  accessGroupScheduleId SERIAL NOT NULL UNIQUE,
  accessGroupScheduleName VARCHAR(255) NOT NULL,
  rrule VARCHAR(255) NOT NULL,
  timeStart VARCHAR(128) NOT NULL,
  timeEnd VARCHAR(128) NOT NULL,
  groupToEntranceId INT REFERENCES AccessGroupsEntranceNtoN (groupToEntranceId),
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (accessGroupScheduleId)
);

CREATE TABLE IF NOT EXISTS EntranceSchedule(
  entranceScheduleId SERIAL NOT NULL UNIQUE,
  entranceScheduleName VARCHAR(255) NOT NULL,
  rrule VARCHAR(255) NOT NULL,
  timeStart VARCHAR(128) NOT NULL,
  timeEnd VARCHAR(128) NOT NULL,
  entranceId INT REFERENCES Entrances (entranceId),
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (entranceScheduleId)
);

CREATE TABLE IF NOT EXISTS Controller(
  controllerId SERIAL NOT NULL UNIQUE,
  controllerName VARCHAR(255),
  controllerIPStatic BOOLEAN NOT NULL,
  controllerIP VARCHAR(255) NOT NULL,
  controllerMAC VARCHAR(255) NOT NULL,
  controllerSerialNo VARCHAR(255) NOT NULL,
  lastOnline TIMESTAMP,
  pinAssignmentConfig VARCHAR(MAX) NOT NULL,
  settingsConfig VARCHAR(MAX) NOT NULL,
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (controllerId)
);

CREATE TABLE IF NOT EXISTS AuthDevice(
  authDeviceId SERIAL NOT NULL UNIQUE,
  authDeviceName VARCHAR(255) NOT NULL,
  authDeviceDirection VARCHAR(255) NOT NULL,
  lastOnline TIMESTAMP,
  masterpin Boolean NOT NULL,
  defaultAuthMethod VARCHAR(255),
  controllerId INT REFERENCES Controller (controllerId),
  entranceId INT REFERENCES Entrances (entranceId),
--  authMethodId INT REFERENCES Entrances (entranceId),
  PRIMARY KEY (authDeviceId)
);

