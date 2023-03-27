--DROP SCHEMA public CASCADE;
--CREATE SCHEMA public;


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
   inputEventsId BIGINT []  ,
   outputActionsId BIGINT[] ,
   triggerSchedulesId BIGINT[],
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
    recipent_User VARCHAR(255) ,
    recipent_Email VARCHAR(255) ,
    PRIMARY KEY (emailSettingsId)
);

CREATE TABLE IF NOT EXISTS SmsSettings(
    smsSettingsId SERIAL NOT NULL UNIQUE,
    smsAPI TEXT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    recipentsms VARCHAR(255),
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

--CREATE SEQUENCE hibernate_sequence;
DROP SEQUENCE IF EXISTS hibernate_sequence;


CREATE TABLE IF NOT EXISTS refreshtoken (
  id serial PRIMARY KEY,
  token text NOT NULL,
  expiry_date timestamp NOT NULL,
  user_id INT REFERENCES users(id)
);
