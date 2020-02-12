SET NAMES utf8;
SET time_zone = `+00:00`;
SET foreign_key_checks = 0;
SET sql_mode = `NO_AUTO_VALUE_ON_ZERO`;

DROP DATABASE IF EXISTS venture_creations;
CREATE DATABASE venture_creations;
USE venture_creations;

DROP TABLE IF EXISTS `accessLevel`;
CREATE TABLE `accessLevel` (
  `accessLevelID` int(11) NOT NULL AUTO_INCREMENT,
  `accessLevelName` varchar(20) NOT NULL,
  PRIMARY KEY (`accessLevelID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `company`;
CREATE TABLE `company` (
  `companyID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`companyID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `fileID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  `file` blob DEFAULT NULL,
  `stepID` char(36) DEFAULT NULL,
  PRIMARY KEY (`fileID`),
  KEY `stepID` (`stepID`),
  CONSTRAINT `file_ibfk_1` FOREIGN KEY (`stepID`) REFERENCES `step` (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `person`;
CREATE TABLE `person` (
  `UUID` char(36) NOT NULL,
  `username` varchar(30) NOT NULL,
  `passwordHash` char(128) NOT NULL,
  `salt` char(32) NOT NULL,
  `fName` varchar(30) NOT NULL,
  `lName` varchar(30) NOT NULL,
  `email` varchar(30) DEFAULT NULL,
  `title` varchar(30) DEFAULT NULL,
  `accessLevelID` int(11) NOT NULL,
  PRIMARY KEY (`UUID`),
  KEY `accessLevelID` (`accessLevelID`),
  CONSTRAINT `person_ibfk_2` FOREIGN KEY (`accessLevelID`) REFERENCES `accessLevel` (`accessLevelID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `personCompany`;
CREATE TABLE `personCompany` (
 `UUID` char(36) NOT NULL,
 `companyID` int(11) NOT NULL,
 PRIMARY KEY (`UUID`,`companyID`),
 KEY `UUID` (`UUID`),
 KEY `companyID` (`companyID`),
 CONSTRAINT `personCompany_ibfk_1` FOREIGN KEY (`UUID`) REFERENCES `person` (`UUID`),
 CONSTRAINT `personCompany_ibfk_2` FOREIGN KEY (`companyID`) REFERENCES `company` (`companyID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `step`;
CREATE TABLE `step` (
  `stepID` int(11) NOT NULL,
  `orderNumber` int(11) DEFAULT NULL,
  `isHighestLevel` boolean NOT NULL,
  `description` varchar(60) DEFAULT NULL,
  `relatedStep` int(11) DEFAULT NULL,
  `UUID` char(36) DEFAULT NULL,
  `verbID` int(11) NOT NULL,
  `fileID` int(11) NOT NULL,
  `workflowID` int(11) NOT NULL,
  `completed` boolean NOT NULL,
  PRIMARY KEY (`stepID`),
  KEY `UUID` (`UUID`),
  KEY `verbID` (`verbID`),
  KEY `fileID` (`fileID`),
  KEY `workflowID` (`workflowID`),
  CONSTRAINT `step_ibfk_1` FOREIGN KEY (`UUID`) REFERENCES `person` (`UUID`),
  CONSTRAINT `step_ibfk_2` FOREIGN KEY (`verbID`) REFERENCES `verb` (`verbID`),
  CONSTRAINT `step_ibfk_3` FOREIGN KEY (`fileID`) REFERENCES `file` (`fileID`),
  CONSTRAINT `step_ibfk_4` FOREIGN KEY (`workflowID`) REFERENCES `workflow` (`workflowID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `userPreferences`;
CREATE TABLE `userPreferences` (
  `UUID` char(36) NOT NULL,
  `signaturePDF` blob DEFAULT NULL,
  `signatureFont` varchar(20) NOT NULL,
  PRIMARY KEY (`UUID`),
  CONSTRAINT `userPreferences_ibfk_1` FOREIGN KEY (`UUID`) REFERENCES `person` (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `verb`;
CREATE TABLE `verb` (
  `verbID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`verbID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `milestone`;
CREATE TABLE `milestone` (
  `milestoneID` int(11) NOT NULL AUTO_INCREMENT,
  `orderNumber` int(11) NOT NULL,
  `name` varchar(60) NOT NULL,
  `description` varchar(60) DEFAULT NULL,
  `createdDate` date NOT NULL,
  `lastUpdatedDate` date NOT NULL,
  `startDate` date DEFAULT NULL,
  `deliveryDate` date DEFAULT NULL,
  `completedDate` date DEFAULT NULL,
  `completed` boolean DEFAULT 0,
  `companyID` int(11) DEFAULT NULL,
  PRIMARY KEY (`milestoneID`),
  KEY `companyID` (`companyID`),
  CONSTRAINT `milestone_ibfk_1` FOREIGN KEY (`companyID`) REFERENCES `company` (`companyID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `workflow`;
CREATE TABLE `workflow` (
  `workflowID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL,
  `createdDate` date NOT NULL,
  `lastUpdatedDate` date NOT NULL,
  `startDate` date DEFAULT NULL,
  `deliveryDate` date DEFAULT NULL,
  `completedDate` date DEFAULT NULL,
  `completed` boolean NOT NULL,
  `milestoneID` int(11) NOT NULL,
  PRIMARY KEY (`workflowID`),
  KEY `milestoneID` (`milestoneID`),
  CONSTRAINT `workflow_ibfk_1` FOREIGN KEY (`milestoneID`) REFERENCES `milestone` (`milestoneID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO venture_creations.accessLevel (accessLevelID, accessLevelName) VALUES
  (1, "Administrator"),
  (2, "Director"),
  (3, "Coach"),
  (4, "Customer"),
  (5, "Provider");

INSERT INTO venture_creations.company (companyID, name) VALUES
  (1, "Venture Creations"),
  (2, "Rochester Institute of Technology"),
  (3, "Sample Company 1"),
  (4, "Sample Company 2");

INSERT INTO venture_creations.milestone (milestoneID, orderNumber, name, description, createdDate, lastUpdatedDate, companyID) VALUES
  (1, 1, "C1 Milestone", "This is a sample C1 milestone.", "2019-5-1", "2019-5-1", 1),
  (2, 2, "C2 Milestone", "This is a sample C2 milestone.", "2019-6-1", "2019-6-1", 1),
  (3, 3, "C3 Milestone", "This is a sample C3 milestone.", "2019-7-1", "2019-7-1", 1),
  (4, 1, "C6 Milestone", "This is a sample C6 milestone.", "2020-1-1", "2020-01-1", 5);

INSERT INTO venture_creations.workflow (workflowID, name, createdDate, lastUpdatedDate, completed, milestoneID) VALUES
  (1, "Sample Workflow 1", "2020-01-01", "2020-05-01", 1, 1),
  (2, "Sample Workflow 2", "2020-02-01", "2020-06-01", 0, 2);

INSERT INTO venture_creations.person (UUID, username, passwordHash, salt, fName, lName, email, title, accessLevelID) VALUES
  ("c5877b03-ac76-4e71-9a88-1c2d9122d474", "admin",     "d0c010cb69151b85bfd3f8b97cd87e336392efdd4b01d3d3c0bdb44e47c02729d51ce46348a98e23364dcf278df7090c35f944e43748f9f2691e252456b69816", "a760a131668ad5883b50e5b78aa53b27", "The",      "Admin",    "administrator@boss.com", "ADMIN",      "1"),
  ("164e2c50-c280-459e-800c-7168e75f4fe3", "director",  "087d53352b229a5c7ade1dfdf80763fea289ded80550c72d5aabe6740774a3a57cb73df8475ff5c3691538d1996eadfce334137486a7e6805c5d14fbfcbc9a80", "0f3e136e699f57976bca04de7547e645", "The",      "Director", "director@example.com", "Director",     "2"),
  ("7d3c0693-fe83-46bf-b878-dc76a61feb06", "coach",     "7e5cac5166038c55ddc931d856e7e9c059bbb21d81be5e3e65f83c201cdfd941e19b58890ca69aeb423c14d7fbb8fc4b0abedd051a0df0abd420ec64fc1132e1", "e53962d306da29d9249791652af8ceed", "A",        "Coach",    "coach@example.com", "Coach", "3"),
  ("20809d5b-7989-4e48-bdde-74033e2f2672", "customer1", "65a8ac0c9d47397bf51072f446341a2cfc48df2188204a3c9582d142b1ea81b9e05a020f83012e3e48bacf866700515515afda905dceec1ff2d44949dc16608e", "3e3c056f0198d253dfb5770386540ef2", "Customer", "Number1",  "user@sample.org", "Software Engineer", "4"),
  ("1bdd74ae-2425-4501-a2d5-0a3039754606", "customer2", "7e324075cc402570cac124fd2d7e6a4f768ed99f8db222427eab73a440d1fd88a594067b8b49fe560106622e32e12c87a5d9113aa0547605bdd6ca31105282d7", "89b627b172367d459f556f41142735a1", "Customer", "Number2",  "person@sample.edu", "DBA",               "4"),
  ("3b47a671-45d6-4769-a1dd-c1aa9f8f8cab", "customer3", "08420a12736c2a8989c935f0aa2f6d876fa7891d33c19cf27e0a5605318be56ed1e7f5792e75cdd320e8322d08bb34f1a88fd01fb697c88b8c7712cdeec55311", "a51c135271c0b9aa2a18ab62d3bb19e0", "Customer", "Number3",  "ThatGuy@omg.com", "Project Manager",   "4"),
  ("26c3edd3-f653-4843-b491-18e0e0a937c1", "provider",  "7dc1789ae1653cc5f3fc6b0e46d541c9fedc453fedd60bcd7abeb429c064de310f7ff91a2f2b8232422b97907e8d7dfe0758e60bfd4b3586fbd3b646cb63ab2a", "3f61b476bae6b35f9031e03f3279d4da", "A",        "Provider", "Vendor@provider.com", "Software Vendor",   "5");

INSERT INTO venture_creations.personCompany (UUID, companyID) VALUES
  ("c5877b03-ac76-4e71-9a88-1c2d9122d474", 1),
  ("c5877b03-ac76-4e71-9a88-1c2d9122d474", 2),
  ("c5877b03-ac76-4e71-9a88-1c2d9122d474", 3),
  ("c5877b03-ac76-4e71-9a88-1c2d9122d474", 4),
  ("20809d5b-7989-4e48-bdde-74033e2f2672", 2),
  ("1bdd74ae-2425-4501-a2d5-0a3039754606", 2),
  ("3b47a671-45d6-4769-a1dd-c1aa9f8f8cab", 2),
  ("26c3edd3-f653-4843-b491-18e0e0a937c1", 2),
  ("164e2c50-c280-459e-800c-7168e75f4fe3", 3),
  ("7d3c0693-fe83-46bf-b878-dc76a61feb06", 4);

INSERT INTO venture_creations.file (fileID, name, file, stepID) VALUES
  (1, "Document.docx", null, null),
  (2, "Image.jpg", null, null),
  (3, "Video.mp4", null, null);

INSERT INTO venture_creations.verb (verbID, name, description) VALUES
  (1, "Sign", "Sign"),
  (2, "Submit", "Submit"),
  (3, "Send", "Complete"),
  (4, "Send", "Fill out");

INSERT INTO venture_creations.step (stepID, orderNumber, isHighestLevel, description, relatedStep, UUID, verbID, fileID, workflowID) VALUES
  (1, 1, 1, "This is a higher level step.", null, null, 1, 1, 1),
  (2, 2, 1, "This is a higher level step.", null, null, 3, 2, 2),
  (3, 1, 0, "This is a second level step.", 2, null, 2, 2, 2),
  (4, 1, 0, "This is a third level step.", 3, null, 2, 2, 2),
  (5, 2, 0, "This is also a third level step.", 3, null, 2, 2, 2),
  (6, 2, 0, "This is a second level step.", 2, null, 2, 2, 2);

INSERT INTO venture_creations.userPreferences (UUID, signaturePDF, signatureFont) VALUES
  ("164e2c50-c280-459e-800c-7168e75f4fe3", null, "Arial"),
  ("7d3c0693-fe83-46bf-b878-dc76a61feb06", null, "Calibri");