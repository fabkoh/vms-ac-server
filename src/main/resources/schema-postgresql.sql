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
  isActive BOOLEAN NOT NULL,
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
  isActive BOOLEAN NOT NULL,
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
  isActive BOOLEAN NOT NULL,
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
  lastSync TIMESTAMP,
  created TIMESTAMP,
  masterController Boolean,
  pinAssignmentConfig VARCHAR(255) NOT NULL,
  settingsConfig VARCHAR(255) NOT NULL,
  deleted BOOLEAN NOT NULL,
--  authDeviceId INT REFERENCES Entrances (entranceId),
  PRIMARY KEY (controllerId)
);

CREATE TABLE IF NOT EXISTS AuthMethod(
  authMethodId SERIAL NOT NULL UNIQUE,
  authMethodDesc VARCHAR(255) NOT NULL,
  authMethodCondition VARCHAR(255) NOT NULL,
  credtypeid INT REFERENCES CredentialType (credTypeId),
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (authMethodId)
);

CREATE TABLE IF NOT EXISTS AuthMethodCredentialTypeNtoN(
  authMethodCredentialsNtoNId SERIAL NOT NULL UNIQUE,
  authMethodId INT REFERENCES AuthMethod (authMethodId),
  credTypeId INT REFERENCES CredentialType (credTypeId),
  deleted BOOLEAN NOT NULL,
  PRIMARY KEY (authMethodCredentialsNtoNId)
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
--  authMethodScheduleId INT REFERENCES AuthMethodSchedule (authMethodScheduleId),
  PRIMARY KEY (authDeviceId)
);

CREATE TABLE IF NOT EXISTS AuthMethodSchedule(
  authMethodScheduleId SERIAL NOT NULL UNIQUE,
  authMethodScheduleName VARCHAR(255) NOT NULL,
  rrule VARCHAR(255) NOT NULL,
  timeStart VARCHAR(128) NOT NULL,
  timeEnd VARCHAR(128) NOT NULL,
  authDeviceId INT REFERENCES AuthDevice (authDeviceId),
  AuthMethodId INT REFERENCES AuthMethod (authMethodId),
  deleted BOOLEAN NOT NULL,
  isActive BOOLEAN NOT NULL,
  PRIMARY KEY (authMethodScheduleId)
);

CREATE TABLE IF NOT EXISTS GENConfigs(
    id SERIAL NOT NULL UNIQUE ,
    controllerId INT REFERENCES Controller(controllerId),
    pinName VARCHAR(25) NOT NULL ,
    status VARCHAR(25) ,
    PRIMARY KEY (id)
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
   eventActionInputConfig JSON,
   PRIMARY KEY (eventActionInputId)
);

CREATE TABLE IF NOT EXISTS EventActionOutputType(
    eventActionOutputId SERIAL NOT NULL UNIQUE,
    eventActionOutputName VARCHAR(255) NOT NULL,
    timerEnabled BOOLEAN NOT NULL,
    eventActionOutputConfig JSON,
    PRIMARY KEY (eventActionOutputId)
);

CREATE TABLE IF NOT EXISTS InputEvent(
    inputEventId SERIAL NOT NULL UNIQUE ,
    timerDuration INT,
    eventActionInputId INT REFERENCES EventActionInputType(eventActionInputId),
    PRIMARY KEY (inputEventId)
);

CREATE TABLE IF NOT EXISTS OutputEvent(
    outputEventId SERIAL NOT NULL UNIQUE ,
    timerDuration INT,
    eventActionOutputId INT REFERENCES EventActionOutputType(eventActionOutputId),
    PRIMARY KEY (outputEventId)
);

CREATE TABLE IF NOT EXISTS TriggerSchedules(
   triggerScheduleId SERIAL NOT NULL UNIQUE ,
   triggerName VARCHAR(255) NOT NULL ,
   rrule VARCHAR(255) NOT NULL ,
   timeStart VARCHAR(128) NOT NULL ,
   timeEnd VARCHAR(128) NOT NULL ,
   deleted BOOLEAN NOT NULL ,
   dstart VARCHAR(255),
   until VARCHAR(255),
   count INT,
   repeatToggle BOOLEAN,
   rruleinterval INT,
   byweekday integer[],
   bymonthday integer[],
   bysetpos integer[],
   bymonth integer[],
   allDay BOOLEAN,
   endOfDay BOOLEAN,
   PRIMARY KEY (triggerScheduleId)
);

CREATE TABLE IF NOT EXISTS EventsManagement(
   eventsManagementId SERIAL NOT NULL UNIQUE ,
   eventsManagementName VARCHAR(255) NOT NULL ,
   deleted BOOLEAN NOT NULL ,
   inputEventsId integer[]  ,
   outputActionsId integer[] ,
   triggerSchedulesId integer[],
   entranceId INT REFERENCES Entrances(entranceId),
   controllerId INT REFERENCES Controller(controllerId),
   PRIMARY KEY (eventsManagementId)
);

CREATE TABLE IF NOT EXISTS VideoRecorder(
    recorderId SERIAL NOT NULL UNIQUE,
    recorderName VARCHAR(255) NOT NULL,
    recorderSerialNumber VARCHAR(255),
    recorderPublicIp VARCHAR(255) NOT NULL,
    recorderPrivateIp VARCHAR(255) NOT NULL,
    recorderPortNumber INT NOT NULL,
    recorderIWSPort INT NOT NULL,
    recorderUsername VARCHAR(255) NOT NULL,
    recorderPassword VARCHAR(255) NOT NULL,
    created TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (recorderId)
);


CREATE TABLE IF NOT EXISTS EmailSettings(
    emailSettingsId SERIAL NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,
    emailPassword VARCHAR(255) NOT NULL,
    hostAddress VARCHAR(255) NOT NULL,
    portNumber VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    custom BOOLEAN NOT NULL DEFAULT FALSE,
    isTLS BOOLEAN NOT NULL DEFAULT FALSE,
    email VARCHAR(255) NOT NULL,
    recipentUsername VARCHAR(255) ,
    recipentEmail VARCHAR(255) ,
    PRIMARY KEY (emailSettingsId)
);

CREATE TABLE IF NOT EXISTS SmsSettings(
    smsSettingsId SERIAL NOT NULL UNIQUE,
    smsAPI TEXT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    RECIPENTSMS TEXT,
    PRIMARY KEY (smsSettingsId)
);

CREATE TABLE IF NOT EXISTS EventsManagementNotification(
    eventsManagementNotificationId SERIAL NOT NULL UNIQUE,
    eventsManagementNotificationType VARCHAR(255),
    eventsManagementNotificationRecipients TEXT NOT NULL,
    eventsManagementNotificationContent TEXT NOT NULL,
    eventsManagementNotificationTitle VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    eventsManagementId INT REFERENCES EventsManagement(eventsManagementId),
    PRIMARY KEY (eventsManagementNotificationId)
);

CREATE TABLE IF NOT EXISTS NotificationLogs(
    notificationLogsId SERIAL NOT NULL UNIQUE,
    notificationLogsStatusCode INT NOT NULL,
    notificationLogsError TEXT NOT NULL,
    timeSent VARCHAR(255) NOT NULL,
    eventsManagementNotificationId INT REFERENCES EventsManagementNotification(eventsManagementNotificationId),
    PRIMARY KEY (notificationLogsId)
);

CREATE TABLE IF NOT EXISTS Roles(
    id SERIAL NOT NULL UNIQUE,
    roleName VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Users(
    id SERIAL NOT NULL UNIQUE,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    mobile VARCHAR(20) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(120) NOT NULL,
    deleted BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users (id),
  FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE SEQUENCE hibernate_sequence;

CREATE TABLE refreshtoken (
  id serial PRIMARY KEY,
  token text NOT NULL,
  expiry_date timestamp NOT NULL,
  user_id INT REFERENCES users(id)
);


INSERT INTO credentialType VALUES ('1','Card','RFID Card',false);
INSERT INTO credentialType VALUES ('2','Face','Face Recognition',false);
INSERT INTO credentialType VALUES ('3','Fingerprint','Fingerprint scanner',false);
INSERT INTO credentialType VALUES ('4','Pin','Digit Pin',false);

INSERT INTO AuthMethod VALUES ('1','Card','AND',null,false);
INSERT INTO AuthMethod VALUES ('2','Face','AND',null,false);
INSERT INTO AuthMethod VALUES ('3','FingerPrint','AND',null,false);
INSERT INTO AuthMethod VALUES ('4','Pin','AND',null,false);
INSERT INTO AuthMethod VALUES ('5','Card + Face','AND',null,false);
INSERT INTO AuthMethod VALUES ('6','Card / Face','OR',null,false);
INSERT INTO AuthMethod VALUES ('7','Card + FingerPrint','AND',null,false);
INSERT INTO AuthMethod VALUES ('8','Card / FingerPrint','OR',null,false);
INSERT INTO AuthMethod VALUES ('9','Card + Pin','AND',null,false);
INSERT INTO AuthMethod VALUES ('10','Face + FingerPrint','AND',null,false);
INSERT INTO AuthMethod VALUES ('11','Face / FingerPrint','OR',null,false);
INSERT INTO AuthMethod VALUES ('12','Face + Pin','AND',null,false);
INSERT INTO AuthMethod VALUES ('13','FingerPrint + Pin','AND',null,false);
INSERT INTO AuthMethod VALUES ('14','Card + Face + FingerPrint','AND',null,false);
INSERT INTO AuthMethod VALUES ('15','Card / Face / FingerPrint','OR',null,false);
INSERT INTO AuthMethod VALUES ('16','Card + Face + Pin','AND',null,false);
INSERT INTO AuthMethod VALUES ('17','Card + FingerPrint + Pin','AND',null,false);
INSERT INTO AuthMethod VALUES ('18','Face + FingerPrint + Pin','AND',null,false);
INSERT INTO AuthMethod VALUES ('19','Card + Face + FingerPrint + Pin','AND',null,false);

INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('1','1','1',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('2','2','2',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('3','3','3',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('4','4','4',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('5','5','1',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('6','5','2',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('7','6','1',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('8','6','2',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('9','7','1',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('10','7','3',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('11','8','1',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('12','8','3',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('13','9','1',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('14','9','4',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('15','10','2',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('16','10','3',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('17','11','2',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('18','11','3',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('19','12','2',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('20','12','4',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('21','13','3',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('22','13','4',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('23','14','1',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('24','14','2',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('25','14','3',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('26','15','1',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('27','15','2',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('28','15','3',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('29','16','1',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('30','16','2',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('31','16','4',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('32','17','1',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('33','17','3',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('34','17','4',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('35','18','2',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('36','18','3',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('37','18','4',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('38','19','1',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('39','19','2',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('40','19','3',false);
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('41','19','4',false);

INSERT INTO EventActionInputType VALUES ('1','AUTHENTICATED SCAN', false, null);
INSERT INTO EventActionInputType VALUES ('2','UNAUTHENTICATED SCAN', false, null);
INSERT INTO EventActionInputType VALUES ('3','EXIT BUTTON PRESSED', false, null);
INSERT INTO EventActionInputType VALUES ('4','DOOR OPENED WITHOUT AUTHENTICATION', true, null);
INSERT INTO EventActionInputType VALUES ('5','DOOR OPENED WITH AUTHENTICATION', true, null);
INSERT INTO EventActionInputType VALUES ('6','FIRE', false, null);
INSERT INTO EventActionInputType VALUES ('7','GEN_IN_1', false, null);
INSERT INTO EventActionInputType VALUES ('8','GEN_IN_2', false, null);
INSERT INTO EventActionInputType VALUES ('9','GEN_IN_3', false, null);
INSERT INTO EventActionInputType VALUES ('10','DOOR CLOSED', false, null);

INSERT INTO EventActionOutputType VALUES ('1','GEN_OUT_1', true, null);
INSERT INTO EventActionOutputType VALUES ('2','GEN_OUT_2', true, null);
INSERT INTO EventActionOutputType VALUES ('3','GEN_OUT_3', true, null);
INSERT INTO EventActionOutputType VALUES ('4','UNLOCK DOOR', false, null);
INSERT INTO EventActionOutputType VALUES ('5','BUZZER', true, null);
INSERT INTO EventActionOutputType VALUES ('6','LED', true, null);
INSERT INTO EventActionOutputType VALUES ('7','NOTIFICATION (SMS)', false, null);
INSERT INTO EventActionOutputType VALUES ('8','NOTIFICATION (EMAIL)', false, null);

INSERT INTO EventActionType VALUES ('1','Authenticated Scans', false);
INSERT INTO EventActionType VALUES ('2','Masterpassword used', false);
INSERT INTO EventActionType VALUES ('3','UnAuthenticated Scans', false);
INSERT INTO EventActionType VALUES ('4','Door Opened', false);
INSERT INTO EventActionType VALUES ('5','Door Closed', false);
INSERT INTO EventActionType VALUES ('6','Warning : Door Opened without authorisation', false);
INSERT INTO EventActionType VALUES ('7','Buzzer Started buzzing', false);
INSERT INTO EventActionType VALUES ('8','Buzzer Stopped buzzing', false);
INSERT INTO EventActionType VALUES ('9','Push Button pressed', false);
INSERT INTO EventActionType VALUES ('10','GEN_IN_1 detected', false);
INSERT INTO EventActionType VALUES ('11','GEN_IN_2 detected', false);
INSERT INTO EventActionType VALUES ('12','GEN_IN_3 detected', false);
INSERT INTO EventActionType VALUES ('13','Valid PIN used', false);
INSERT INTO EventActionType VALUES ('14','Invalid PIN used', false);

INSERT INTO smssettings (smsapi, enabled) VALUES ('isssecurity', true);
INSERT INTO emailsettings (username, email, emailpassword, hostaddress, portnumber, enabled, custom, istls) VALUES ('Zephan Wong Kai En', 'zephan.wong@isssecurity.sg', 'avdfhveswyonpuwq', 'smtp.gmail.com', '587', true, true, true);
INSERT INTO notificationlogs (notificationlogsstatuscode, notificationlogserror, timesent) VALUES (400, 'TestingError', '10-22-2022 03:50:39');
INSERT INTO videorecorder (recorderiwsport, recordername, recorderpassword, recorderportnumber, recorderprivateip, recorderpublicip, recorderserialnumber, recorderusername, deleted, created) VALUES('7681', 'testing', 'ISSNVRTest01', '8085','192.168.1.172','118.201.255.164','DS-7616NI-I21620210923CCRRG74241239WCVU','admin',false,'2023-03-16 11:26:39.641753')
