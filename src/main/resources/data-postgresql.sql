
INSERT INTO credentialType VALUES ('1','Card','RFID Card',false) ON CONFLICT DO NOTHING;
INSERT INTO credentialType VALUES ('2','Face','Face Recognition',false)ON CONFLICT DO NOTHING;
INSERT INTO credentialType VALUES ('3','Fingerprint','Fingerprint scanner',false)ON CONFLICT DO NOTHING;
INSERT INTO credentialType VALUES ('4','Pin','Digit Pin',false)ON CONFLICT DO NOTHING;

INSERT INTO AuthMethod VALUES ('1','Card','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('2','Face','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('3','FingerPrint','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('4','Pin','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('5','Card + Face','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('6','Card / Face','OR',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('7','Card + FingerPrint','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('8','Card / FingerPrint','OR',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('9','Card + Pin','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('10','Face + FingerPrint','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('11','Face / FingerPrint','OR',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('12','Face + Pin','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('13','FingerPrint + Pin','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('14','Card + Face + FingerPrint','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('15','Card / Face / FingerPrint','OR',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('16','Card + Face + Pin','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('17','Card + FingerPrint + Pin','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('18','Face + FingerPrint + Pin','AND',null,false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethod VALUES ('19','Card + Face + FingerPrint + Pin','AND',null,false) ON CONFLICT DO NOTHING;

INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('1','1','1',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('2','2','2',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('3','3','3',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('4','4','4',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('5','5','1',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('6','5','2',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('7','6','1',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('8','6','2',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('9','7','1',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('10','7','3',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('11','8','1',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('12','8','3',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('13','9','1',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('14','9','4',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('15','10','2',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('16','10','3',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('17','11','2',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('18','11','3',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('19','12','2',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('20','12','4',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('21','13','3',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('22','13','4',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('23','14','1',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('24','14','2',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('25','14','3',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('26','15','1',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('27','15','2',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('28','15','3',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('29','16','1',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('30','16','2',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('31','16','4',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('32','17','1',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('33','17','3',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('34','17','4',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('35','18','2',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('36','18','3',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('37','18','4',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('38','19','1',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('39','19','2',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('40','19','3',false) ON CONFLICT DO NOTHING;
INSERT INTO AuthMethodCredentialTypeNtoN VALUES ('41','19','4',false) ON CONFLICT DO NOTHING;

INSERT INTO EventActionInputType VALUES ('1','AUTHENTICATED SCAN', false, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionInputType VALUES ('2','UNAUTHENTICATED SCAN', false, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionInputType VALUES ('3','EXIT BUTTON PRESSED', false, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionInputType VALUES ('4','DOOR OPENED WITHOUT AUTHENTICATION', true, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionInputType VALUES ('5','DOOR OPENED WITH AUTHENTICATION', true, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionInputType VALUES ('6','FIRE', false, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionInputType VALUES ('7','GEN_IN_1', false, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionInputType VALUES ('8','GEN_IN_2', false, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionInputType VALUES ('9','GEN_IN_3', false, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionInputType VALUES ('10','DOOR CLOSED', false, null) ON CONFLICT DO NOTHING;

INSERT INTO EventActionOutputType VALUES ('1','GEN_OUT_1', true, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionOutputType VALUES ('2','GEN_OUT_2', true, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionOutputType VALUES ('3','GEN_OUT_3', true, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionOutputType VALUES ('4','UNLOCK DOOR', false, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionOutputType VALUES ('5','BUZZER', true, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionOutputType VALUES ('6','LED', true, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionOutputType VALUES ('7','NOTIFICATION (SMS)', false, null) ON CONFLICT DO NOTHING;
INSERT INTO EventActionOutputType VALUES ('8','NOTIFICATION (EMAIL)', false, null) ON CONFLICT DO NOTHING;

INSERT INTO EventActionType VALUES ('1','Authenticated Scans', false) ON CONFLICT DO NOTHING;
INSERT INTO EventActionType VALUES ('2','Masterpassword used', false) ON CONFLICT DO NOTHING;
INSERT INTO EventActionType VALUES ('3','UnAuthenticated Scans', false) ON CONFLICT DO NOTHING;
INSERT INTO EventActionType VALUES ('4','Door Opened', false) ON CONFLICT DO NOTHING;
INSERT INTO EventActionType VALUES ('5','Door Closed', false) ON CONFLICT DO NOTHING;
INSERT INTO EventActionType VALUES ('6','Warning : Door Opened without authorisation', false) ON CONFLICT DO NOTHING;
INSERT INTO EventActionType VALUES ('7','Buzzer Started buzzing', false) ON CONFLICT DO NOTHING;
INSERT INTO EventActionType VALUES ('8','Buzzer Stopped buzzing', false) ON CONFLICT DO NOTHING;
INSERT INTO EventActionType VALUES ('9','Push Button pressed', false) ON CONFLICT DO NOTHING;
INSERT INTO EventActionType VALUES ('10','GEN_IN_1 detected', false) ON CONFLICT DO NOTHING;
INSERT INTO EventActionType VALUES ('11','GEN_IN_2 detected', false) ON CONFLICT DO NOTHING;
INSERT INTO EventActionType VALUES ('12','GEN_IN_3 detected', false) ON CONFLICT DO NOTHING;
INSERT INTO EventActionType VALUES ('13','Valid PIN used', false) ON CONFLICT DO NOTHING;
INSERT INTO EventActionType VALUES ('14','Invalid PIN used', false) ON CONFLICT DO NOTHING;

INSERT INTO smssettings (smsSettingsId, smsapi, enabled) VALUES (1, 'isssecurity', true) ON CONFLICT DO NOTHING;
INSERT INTO emailsettings (emailSettingsId, username, email, emailpassword, hostaddress, portnumber, enabled, custom, istls) VALUES (1, 'Zephan Wong Kai En', 'zephan.wong@isssecurity.sg', 'avdfhveswyonpuwq', 'smtp.gmail.com', '587', true, true, true) ON CONFLICT DO NOTHING;
INSERT INTO notificationlogs (notificationLogsId, notificationlogsstatuscode, notificationlogserror, timesent) VALUES (1,400, 'TestingError', '10-22-2022 03:50:39') ON CONFLICT DO NOTHING;
INSERT INTO videorecorder (recorderId, recorderiwsport, recordername, recorderpassword, recorderportnumber, recorderprivateip, recorderpublicip, recorderserialnumber, recorderusername, deleted, created, autoportforwarding) VALUES(1,'7681', 'testing', 'ISSNVRTest01', '8085','192.168.1.172','118.201.255.164','DS-7616NI-I21620210923CCRRG74241239WCVU','admin',false,'2023-03-16 11:26:39.641753', false) ON CONFLICT DO NOTHING;
