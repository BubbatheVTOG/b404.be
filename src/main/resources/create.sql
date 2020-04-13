DROP DATABASE IF EXISTS DB_DATABASE;
CREATE DATABASE DB_DATABASE;
USE DB_DATABASE;

SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO';

DROP TABLE IF EXISTS `accessLevel`;
CREATE TABLE `accessLevel` (
  `accessLevelID` int(11) NOT NULL AUTO_INCREMENT,
  `accessLevelName` varchar(255) NOT NULL,
  PRIMARY KEY (`accessLevelID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `person`;
CREATE TABLE `person` (
  `UUID` char(36) NOT NULL,
  `username` varchar(30) NOT NULL,
  `passwordHash` char(128) NOT NULL,
  `salt` char(32) NOT NULL,
  `fName` varchar(255) NOT NULL,
  `lName` varchar(255) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `accessLevelID` int(11) NOT NULL,
  `signature` longblob DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `company`;
CREATE TABLE `company` (
  `companyID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`companyID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `personCompany`;
CREATE TABLE `personCompany` (
 `UUID` char(36) NOT NULL,
 `companyID` int(11) NOT NULL,
 PRIMARY KEY (`UUID`,`companyID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `fileID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `file` longblob DEFAULT NULL,
  `confidential` boolean NOT NULL,
  PRIMARY KEY (`fileID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `verb`;
CREATE TABLE `verb` (
  `verbID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`verbID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `step`;
CREATE TABLE `step` (
  `stepID` int(11) NOT NULL AUTO_INCREMENT,
  `orderNumber` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `parentStepID` int(11) DEFAULT NULL,
  `UUID` char(36) DEFAULT NULL,
  `verbID` int(11) DEFAULT NULL,
  `fileID` int(11) DEFAULT NULL,
  `workflowID` int(11) NOT NULL,
  `asynchronous` boolean NOT NULL DEFAULT 0,
  `completed` boolean NOT NULL DEFAULT 0,
  PRIMARY KEY (`stepID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `milestone`;
CREATE TABLE `milestone` (
  `milestoneID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `createdDate` date NOT NULL,
  `lastUpdatedDate` date NOT NULL,
  `startDate` date DEFAULT NULL,
  `deliveryDate` date DEFAULT NULL,
  `completedDate` date DEFAULT NULL,
  `archived` boolean DEFAULT 0,
  `companyID` int(11) DEFAULT NULL,
  PRIMARY KEY (`milestoneID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `workflow`;
CREATE TABLE `workflow` (
  `workflowID` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `createdDate` date NOT NULL,
  `lastUpdatedDate` date NOT NULL,
  `startDate` date DEFAULT NULL,
  `deliveryDate` date DEFAULT NULL,
  `completedDate` date DEFAULT NULL,
  `archived` boolean DEFAULT 0,
  `milestoneID` int(11) DEFAULT NULL,
  PRIMARY KEY (`workflowID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `person`
ADD CONSTRAINT `fk_person_1`
FOREIGN KEY (`accessLevelID`) REFERENCES `accessLevel`(`accessLevelID`);

ALTER TABLE `personCompany`
ADD CONSTRAINT `fk_personCompany_1`
FOREIGN KEY (`UUID`) REFERENCES `person`(`UUID`) ON DELETE CASCADE;

ALTER TABLE `personCompany`
ADD CONSTRAINT `fk_personCompany_2`
FOREIGN KEY (`companyID`) REFERENCES `company`(`companyID`) ON DELETE CASCADE;

ALTER TABLE `milestone`
ADD CONSTRAINT `fk_milestone_1`
FOREIGN KEY (`companyID`) REFERENCES `company`(`companyID`) ON DELETE CASCADE;

ALTER TABLE `workflow`
ADD CONSTRAINT `fk_workflow_1`
FOREIGN KEY (`milestoneID`) REFERENCES `milestone`(`milestoneID`) ON DELETE CASCADE;

ALTER TABLE `step`
ADD CONSTRAINT `fk_step_1`
FOREIGN KEY (`UUID`) REFERENCES `person`(`UUID`) ON DELETE SET NULL,
ADD CONSTRAINT `fk_step_2`
FOREIGN KEY (`verbID`) REFERENCES `verb`(`verbID`),
ADD CONSTRAINT `fk_step_3`
FOREIGN KEY (`fileID`) REFERENCES `file`(`fileID`),
ADD CONSTRAINT `fk_step_4`
FOREIGN KEY (`workflowID`) REFERENCES `workflow`(`workflowID`) ON DELETE CASCADE;

INSERT INTO DB_DATABASE.accessLevel (accessLevelID, accessLevelName) VALUES
  (1, "Administrator"),
  (2, "Director"),
  (3, "Coach"),
  (4, "Customer"),
  (5, "Provider");

INSERT INTO DB_DATABASE.company (companyID, name) VALUES
  (1, "Venture Creations"),
  (2, "Rochester Institute of Technology"),
  (3, "Sample Company 1"),
  (4, "Sample Company 2");

INSERT INTO DB_DATABASE.milestone (milestoneID, name, description, createdDate, startDate, deliveryDate, lastUpdatedDate, completedDate, archived, companyID) VALUES
  (1, "C6 Milestone", "This is a sample C6 milestone.", "2019-01-01", "2019-01-01", "2019-06-01", "2019-06-01", "2019-06-01", 1, 2),   /* Completed milestone */
  (2, "C1 Milestone", "This is a sample C1 milestone.", "2019-05-01", "2019-05-01", "2019-10-01", "2019-09-01", null, 0, 1),           /* In progress milestone */
  (3, "C2 Milestone", "This is a sample C2 milestone.", "2019-05-01", "2019-10-01", "2019-12-01", "2019-05-01", null, 0, 1);           /* Newly created milestone */

INSERT INTO DB_DATABASE.workflow (workflowID, name, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, milestoneID) VALUES
  (1, "Template Workflow", "2019-01-01", "2019-02-01", "2019-01-01", "2019-02-01", "2019-02-01",      0, null),   /* Template Workflow with steps */
  (2, "Milestone 1 Workflow 1", "2019-01-01", "2019-02-01", "2019-01-01", "2019-02-01", "2019-02-01", 1, 1),      /* Completed workflow to milestone 1 */
  (3, "Milestone 1 Workflow 2", "2019-01-01", "2019-04-01", "2019-02-01", "2019-04-01", "2019-04-01", 1, 1),      /* Completed workflow to milestone 1 */
  (4, "Milestone 1 Workflow 3", "2019-01-01", "2019-06-01", "2019-02-01", "2019-06-01", "2019-06-01", 1, 1),      /* Completed workflow to milestone 1 */
  (5, "Milestone 2 Workflow 1", "2019-05-01", "2019-10-01", "2019-05-01", "2019-10-01", "2019-10-01", 0, 2),      /* Completed workflow to milestone 2 */
  (6, "Milestone 2 Workflow 2", "2019-05-01", "2020-02-01", "2020-03-01", "2020-06-01", null,         0, 2),      /* In progress workflow to milestone 2 */
  (7, "Milestone 2 Workflow 3", "2020-01-01", "2020-01-01", "2020-06-01", "2020-08-01", null,         0, 2),      /* Not Started workflow to milestone 2 */
  (8, "Milestone 3 Workflow 1", "2020-01-01", "2020-01-01", "2020-08-01", "2020-10-01", null,         0, 3),      /* Not started workflow to milestone 3 */
  (9, "Milestone 3 Workflow 1", "2020-01-01", "2020-01-01", "2020-10-01", "2020-12-01", null,         0, 3);      /* Not started workflow to milestone 3 */

INSERT INTO DB_DATABASE.person (UUID, username, passwordHash, salt, fName, lName, title, accessLevelID, signature) VALUES
  ("c5877b03-ac76-4e71-9a88-1c2d9122d474", "admin", "f42b201639a6a0c5d251b266e4468f44fec5f0f02f7b931f9de2ea512b31ec4471018103f4c0780f8276378e50169ec57c04fb9e31e46d2e7368cb40b610c3a7", "a760a131668ad5883b50e5b78aa53b27", "The", "Admin", "ADMIN", 1, ""),
  ("164e2c50-c280-459e-800c-7168e75f4fe3", "director", "6a2cdb18e51914b59d624a5d8370504996d14ad5dc1ac3f260f8892bf5f87f75f5608cb7e0c7478bafa481097809a414836b2fba486d64ba5af7b07e35638ac3", "0f3e136e699f57976bca04de7547e645", "A", "Director", "CEO", 2, ""),
  ("7d3c0693-fe83-46bf-b878-dc76a61feb06", "coach", "65bed94fdd574cd4609cbd4cfd9db28f7e41d8a62dd35e92f5de30e5f34cd25dd96c38cef55fad04f24f895bcf42f32f649f5142878a278dde0ef560650d3eb6", "e53962d306da29d9249791652af8ceed", "A", "Coach", "Coach", 3, ""),
  ("20809d5b-7989-4e48-bdde-74033e2f2672", "customer1", "1c6d9f0dacd79a80d18bad2c5c4d68e4ac9ace731d45a74c7388a5a856e97f1d2d07dae717d85ab7579dbfe32bafe930d9436f97e6c6f03465dacd338fe225d5", "3e3c056f0198d253dfb5770386540ef2", "Customer", "Number1", "Project Manager", 4, ""),
  ("1bdd74ae-2425-4501-a2d5-0a3039754606", "customer2", "27c5abc3b940a6bbb89395e1cfd5fb05784f82b75b097390f4f4ca421a58c782ba09394feae6229188a30db46499a9d55829eaf2240f8b1f3ed69429a84e2617", "89b627b172367d459f556f41142735a1", "Customer", "Number2", "DBA", 4, ""),
  ("3b47a671-45d6-4769-a1dd-c1aa9f8f8cab", "customer3", "15fd55bc05e5d1f08fca598c790264187a9517efc87e4c170f89482473a20492e6d20e5c41a212ec59191086c9bb45b76543a1ae4689f5a87b4382d19b40ba80", "a51c135271c0b9aa2a18ab62d3bb19e0", "Customer", "Number2", "Back-End Engineer", 4, ""),
  ("26c3edd3-f653-4843-b491-18e0e0a937c1", "provider", "9d3e80f867a7a4e2f9041b9d0459f02077bac59985a3ac4293c84a93882783b0b0892c1f7d32c21ac08cef7ee397f3212b49274e1670ff4e331cbb95005f33d1", "3f61b476bae6b35f9031e03f3279d4da", "Random", "Provider", "Third-Party Vendor", 5, "");

INSERT INTO DB_DATABASE.personCompany (UUID, companyID) VALUES
  ("164e2c50-c280-459e-800c-7168e75f4fe3", 1), /* Director to all companies */
  ("164e2c50-c280-459e-800c-7168e75f4fe3", 2),
  ("164e2c50-c280-459e-800c-7168e75f4fe3", 3),
  ("164e2c50-c280-459e-800c-7168e75f4fe3", 4),
  ("7d3c0693-fe83-46bf-b878-dc76a61feb06", 1), /* Coach to venture_creations and RIT */
  ("7d3c0693-fe83-46bf-b878-dc76a61feb06", 2),
  ("20809d5b-7989-4e48-bdde-74033e2f2672", 2), /* Customer1 to Rochester Institute of Technology */
  ("1bdd74ae-2425-4501-a2d5-0a3039754606", 3), /* Customer2 to Sample Company1 */
  ("3b47a671-45d6-4769-a1dd-c1aa9f8f8cab", 4); /* Customer3 to Sample Company2 */

INSERT INTO DB_DATABASE.file (fileID, name, file, confidential) VALUES
  (0, "No File Linked", "", 0),
  (1, "Document.docx", "", 0),
  (2, "Image.jpg", "", 0),
  (3, "Video.mp4", "", 0),
  (4, "No Linked File", "", 0);

INSERT INTO DB_DATABASE.verb (verbID, name) VALUES
  (0, "No Action Required"),
  (1, "Sign"),
  (2, "Submit"),
  (3, "Send"),
  (4, "Approve");

INSERT INTO DB_DATABASE.step (stepID, orderNumber, description, parentStepID, UUID, verbID, fileID, workflowID, asynchronous, completed) VALUES
  (1, 1, "This is a higher level step.", null, null, 1, 1, 1, 0, 0),
  (2, 2, "This is a higher level step.", null, null, 3, 2, 1, 0, 0),
  (3, 1, "This is a second level step.", 2,    null, 2, 2, 1, 0, 0),
  (4, 1, "This is a third level step.",  3,    null, 2, 2, 1, 1, 0),
  (5, 2, "This is a third level step.",  3,    null, 2, 2, 1, 1, 0),
  (6, 2, "This is a second level step.", 2,    null, 2, 2, 1, 0, 0);
