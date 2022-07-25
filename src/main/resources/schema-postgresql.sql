DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;

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
  accessGroupId SERIAL ,
  accessGroupName VARCHAR(255) NOT NULL,
  accessGroupDesc VARCHAR(5000),
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (accessGroupId)
);

CREATE TABLE IF NOT EXISTS Persons (
  personId SERIAL ,
  personFirstName VARCHAR(128) NOT NULL,
  personLastName VARCHAR(128) NOT NULL,
  personUID VARCHAR(128) NOT NULL,
  personMobileNumber VARCHAR(128),
  personEmail VARCHAR(128),
  accessGroupId INT REFERENCES AccessGroups (accessGroupId) ,
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (personId)
);

CREATE TABLE IF NOT EXISTS OrgGroups (
  orgGroupId SERIAL NOT NULL UNIQUE,
  orgGroupName VARCHAR(255) NOT NULL,
  orgGroupDesc VARCHAR(5000),
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
  thirdPartyOption VARCHAR(255),
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
  pendingIP VARCHAR(255),
  controllerMAC VARCHAR(255) NOT NULL,
  controllerSerialNo VARCHAR(255) NOT NULL,
  lastOnline TIMESTAMP,
  created TIMESTAMP,
  masterController Boolean,
  pinAssignmentConfig VARCHAR(5000) NOT NULL,
  settingsConfig VARCHAR(5000) NOT NULL,
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (controllerId)
);

CREATE TABLE IF NOT EXISTS GENConfigs(
     id SERIAL PRIMARY KEY ,
     controllerId INT REFERENCES Controller(controllerId),
     pinName VARCHAR(25) NOT NULL ,
     status VARCHAR(25)
);

CREATE TABLE IF NOT EXISTS AuthMethod(
  authMethodId SERIAL NOT NULL UNIQUE,
  authMethodDesc VARCHAR(255) NOT NULL,
  authMethodCondition VARCHAR(255) NOT NULL,
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (authMethodId)
);

CREATE TABLE IF NOT EXISTS AuthDevice(
  authDeviceId SERIAL NOT NULL UNIQUE,
  authDeviceName VARCHAR(255) NOT NULL,
  authDeviceDirection VARCHAR(255) NOT NULL,
  lastOnline TIMESTAMP,
  masterpin Boolean NOT NULL,
  defaultAuthMethod INT REFERENCES AuthMethod (authMethodId),
  controllerId INT REFERENCES Controller (controllerId),
  entranceId INT REFERENCES Entrances (entranceId),
--  authMethodId INT REFERENCES Entrances (entranceId),
  PRIMARY KEY (authDeviceId)
);

CREATE TABLE IF NOT EXISTS AuthMethodCredentialTypeNtoN(
  authMethodCredentialsNtoNId SERIAL NOT NULL UNIQUE,
  authMethodId INT REFERENCES AuthMethod (authMethodId) NOT NULL,
  credTypeId INT REFERENCES CredentialType (credTypeId) NOT NULL,
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (authMethodCredentialsNtoNId)
);

CREATE TABLE IF NOT EXISTS EventActionType(
  eventActionTypeId SERIAL NOT NULL UNIQUE,
  eventActionTypeName VARCHAR(255) NOT NULL,
  isTimerEnabled VARCHAR(255) NOT NULL,
  PRIMARY KEY (eventActionTypeId)
);

CREATE TABLE IF NOT EXISTS Events(
    eventId SERIAL NOT NULL UNIQUE,
    eventTime VARCHAR(255) NOT NULL,
    direction VARCHAR(255),
    entranceId INT REFERENCES Entrances (entranceId),
    personId INT REFERENCES Persons (personId),
    accessGroupId INT REFERENCES AccessGroups (accessGroupId),
    eventActionTypeId INT REFERENCES EventActionType (eventActionTypeId),
    controllerId INT REFERENCES Controller (controllerId),
    deleted BOOLEAN NOT NULL,
    --  linkedEventsId INT REFERENCES Controller (controllerId),
    PRIMARY KEY (eventId)
);

CREATE TABLE IF NOT EXISTS EventActionInputType(
    eventActionInputId SERIAL NOT NULL UNIQUE,
    eventActionInputName VARCHAR(255) NOT NULL,
    timerEnabled BOOLEAN NOT NULL,
    eventActionInputConfig VARCHAR(5000),
    PRIMARY KEY (eventActionInputId)
);

CREATE TABLE IF NOT EXISTS EventActionOutputType(
   eventActionOutputId SERIAL NOT NULL UNIQUE,
   eventActionOutputName VARCHAR(255) NOT NULL,
   timerEnabled BOOLEAN NOT NULL,
   eventActionOutputConfig VARCHAR(5000),
   PRIMARY KEY (eventActionOutputId)
);

CREATE TABLE IF NOT EXISTS InputEvent(
    inputEventId SERIAL NOT NULL UNIQUE ,
    timerDuration INT,
    eventActionInputId INT REFERENCES EventActionInputType(eventActionInputId)
);

CREATE TABLE IF NOT EXISTS OutputEvent(
    outputEventId SERIAL NOT NULL UNIQUE ,
    timerDuration INT,
    eventActionOutputId INT REFERENCES EventActionOutputType(eventActionOutputId)
);

CREATE TABLE IF NOT EXISTS TriggerSchedules(
    triggerScheduleId SERIAL NOT NULL UNIQUE ,
    triggerName VARCHAR(255) NOT NULL ,
    rrule VARCHAR(255) NOT NULL ,
    timeStart VARCHAR(128) NOT NULL ,
    timeEnd VARCHAR(128) NOT NULL ,
    deleted BOOLEAN NOT NULL ,
    eventsManagementId INT
);

CREATE TABLE IF NOT EXISTS EventsManagement(
    eventsManagementId SERIAL NOT NULL UNIQUE ,
    eventsManagementName VARCHAR(255) NOT NULL ,
    deleted BOOLEAN NOT NULL ,
    inputEventsId BIGINT[] ,
    outputActionsId BIGINT[]  ,
    entranceId INT REFERENCES Entrances(entranceId),
    controllerId INT REFERENCES Controller(controllerId)
);

CREATE FUNCTION get_gen_config() RETURNS trigger AS '
    BEGIN
        for counter in 1..3 loop
            INSERT INTO GENConfigs(controllerId, pinName) VALUES (NEW.controllerId, '''' || counter);
        end loop;
        RETURN NEW;
    END
' LANGUAGE plpgsql;

CREATE TRIGGER get_gen_config AFTER INSERT ON Controller
    FOR EACH ROW EXECUTE PROCEDURE get_gen_config();