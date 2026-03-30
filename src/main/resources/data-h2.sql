MERGE INTO AccessGroups (accessGroupId, accessGroupName, accessGroupDesc, isActive, deleted) KEY (accessGroupId) VALUES (1, 'Visitor Access Group', 'Visitors who self-registered for access', true, false);

MERGE INTO emailsettings (emailSettingsId, username, email, emailpassword, hostaddress, portnumber, enabled, custom, istls) KEY (emailSettingsId) VALUES (1, 'Etlas', 'soojunneng01@gmail.com', 'kphc nsyj aasj drde', 'smtp.gmail.com', '587', true, true, true);

MERGE INTO smssettings (smsSettingsId, smsapi, enabled) KEY (smsSettingsId) VALUES (1, 'isssecurity', true);

