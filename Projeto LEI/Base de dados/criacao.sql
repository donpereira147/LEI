create database nova;
use nova;

-- Disable foreign key checks so tables can be dropped/created in any order.
SET FOREIGN_KEY_CHECKS = 0; 

-- Concept file.
DROP TABLE IF EXISTS concept;
CREATE TABLE concept (
    id NUMERIC(18) UNSIGNED NOT NULL PRIMARY KEY,
    effectiveTime DATE NOT NULL,
    active BOOLEAN NOT NULL,
    moduleId NUMERIC(18) UNSIGNED NOT NULL,
    definitionStatusId NUMERIC(18) UNSIGNED NOT NULL,
    FOREIGN KEY (moduleId) REFERENCES concept(id),
    FOREIGN KEY (definitionStatusId) REFERENCES concept(id)
) CHARACTER SET utf8;

LOAD DATA LOCAL INFILE 'C:/Users/perei/Downloads/Terminology/sct2_Concept_Snapshot_INT_20140731.txt' INTO TABLE concept LINES TERMINATED BY '\r\n' IGNORE 1 LINES
(@id,@effectiveTime,@active,@moduleId,@definitionStatusId)
SET id = @id,
effectiveTime = @effectiveTime,
active = @active,
moduleId = @moduleId,
definitionStatusId = @definitionStatusId;


-- Description file.
DROP TABLE IF EXISTS description;
CREATE TABLE description (
    id NUMERIC(18) UNSIGNED NOT NULL PRIMARY KEY,
    effectiveTime DATE NOT NULL,
    active BOOLEAN NOT NULL,
    moduleId NUMERIC(18) UNSIGNED NOT NULL,
    conceptId NUMERIC(18) UNSIGNED NOT NULL,
    languageCode CHAR(2) NOT NULL,
    typeId NUMERIC(18) UNSIGNED NOT NULL,
    term VARCHAR(255) NOT NULL,
    caseSignificanceId NUMERIC(18) UNSIGNED NOT NULL,
    FOREIGN KEY (moduleId) REFERENCES concept(id),
    FOREIGN KEY (conceptId) REFERENCES concept(id),
    FOREIGN KEY (typeId) REFERENCES concept(id),
    FOREIGN KEY (caseSignificanceId) REFERENCES concept(id)
) CHARACTER SET utf8;

LOAD DATA LOCAL INFILE 'C:/Users/perei/Downloads/Terminology/sct2_Description_Snapshot-en_INT_20140731.txt' INTO TABLE description LINES TERMINATED BY '\r\n' IGNORE 1 LINES
(@id,@effectiveTime,@active,@moduleId,@conceptId,@languageCode,@typeId,@term,@caseSignificanceId)
SET id = @id,
effectiveTime = @effectiveTime,
active = @active,
moduleId = @moduleId,
conceptId = @conceptId,
languageCode = @languageCode,
typeId = @typeId,
term = @term,
caseSignificanceId=@caseSignificanceId;


-- Identifier file.
DROP TABLE IF EXISTS identifier;
CREATE TABLE identifier (
    identifierSchemeId NUMERIC(18) UNSIGNED NOT NULL,
    alternateIdentifier VARCHAR(255) NOT NULL,
    effectiveTime DATE NOT NULL,
    active BOOLEAN NOT NULL,
    moduleId NUMERIC(18) UNSIGNED NOT NULL,
    referencedComponentId NUMERIC(18) UNSIGNED NOT NULL,
    FOREIGN KEY (moduleId) REFERENCES concept(id)
) CHARACTER SET utf8;

LOAD DATA LOCAL INFILE 'C:/Users/perei/Downloads/Terminology/sct2_Identifier_Snapshot_INT_20140731.txt' INTO TABLE identifier LINES TERMINATED BY '\r\n' IGNORE 1 LINES
(@identifierSchemeId,@alternateIdentifier,@effectiveTime,@active,@moduleId,@referencedComponentId)
SET identifierSchemeId = @identifierSchemeId,
alternateIdentifier = @alternateIdentifier,
effectiveTime = @effectiveTime,
active = @active,
moduleId = @moduleId,
referencedComponentId = @referencedComponentId;


-- Relationship file.
DROP TABLE IF EXISTS relationship;
CREATE TABLE relationship (
    id NUMERIC(18) UNSIGNED NOT NULL PRIMARY KEY,
    effectiveTime DATE NOT NULL,
    active BOOLEAN NOT NULL,
    moduleId NUMERIC(18) UNSIGNED NOT NULL,
    sourceId NUMERIC(18) UNSIGNED NOT NULL,
    destinationId NUMERIC(18) UNSIGNED NOT NULL,
    relationshipGroup INT UNSIGNED NOT NULL,
    typeId NUMERIC(18) UNSIGNED NOT NULL,
    characteristicTypeId NUMERIC(18) UNSIGNED NOT NULL,
    modifierId NUMERIC(18) UNSIGNED NOT NULL,
    FOREIGN KEY (moduleId) REFERENCES concept(id),
    FOREIGN KEY (sourceId) REFERENCES concept(id),
    FOREIGN KEY (destinationId) REFERENCES concept(id),
    FOREIGN KEY (typeId) REFERENCES concept(id),
    FOREIGN KEY (characteristicTypeId) REFERENCES concept(id),
    FOREIGN KEY (modifierId) REFERENCES concept(id)
) CHARACTER SET utf8;

LOAD DATA LOCAL INFILE 'C:/Users/perei/Downloads/Terminology/sct2_Relationship_Snapshot_INT_20140731.txt' INTO TABLE relationship LINES TERMINATED BY '\r\n' IGNORE 1 LINES
(@id,@effectiveTime,@active,@moduleId,@sourceId,@destinationId,@relationshipGroup,@typeId,@characteristicTypeId,@modifierId)
SET id = @id,
effectiveTime = @effectiveTime,
active = @active,
moduleId = @moduleId,
sourceId = @sourceId,
destinationId = @destinationId,
relationshipGroup = @relationshipGroup,
typeId = @typeId,
characteristicTypeId = @characteristicTypeId,
modifierId = @modifierId;


-- Stated Relationship file.
DROP TABLE IF EXISTS statedrelationship;
CREATE TABLE statedrelationship (
    id NUMERIC(18) UNSIGNED NOT NULL PRIMARY KEY,
    effectiveTime DATE NOT NULL,
    active BOOLEAN NOT NULL,
    moduleId NUMERIC(18) UNSIGNED NOT NULL,
    sourceId NUMERIC(18) UNSIGNED NOT NULL,
    destinationId NUMERIC(18) UNSIGNED NOT NULL,
    relationshipGroup INT UNSIGNED NOT NULL,
    typeId NUMERIC(18) UNSIGNED NOT NULL,
    characteristicTypeId NUMERIC(18) UNSIGNED NOT NULL,
    modifierId NUMERIC(18) UNSIGNED NOT NULL,
    FOREIGN KEY (moduleId) REFERENCES concept(id),
    FOREIGN KEY (sourceId) REFERENCES concept(id),
    FOREIGN KEY (destinationId) REFERENCES concept(id),
    FOREIGN KEY (typeId) REFERENCES concept(id),
    FOREIGN KEY (characteristicTypeId) REFERENCES concept(id),
    FOREIGN KEY (modifierId) REFERENCES concept(id)
) CHARACTER SET utf8;

LOAD DATA LOCAL INFILE 'C:/Users/perei/Downloads/Terminology/sct2_StatedRelationship_Snapshot_INT_20140731.txt' INTO TABLE statedrelationship LINES TERMINATED BY '\r\n' IGNORE 1 LINES
(@id,@effectiveTime,@active,@moduleId,@sourceId,@destinationId,@relationshipGroup,@typeId,@characteristicTypeId,@modifierId)
SET id = @id,
effectiveTime = @effectiveTime,
active = @active,
moduleId = @moduleId,
sourceId = @sourceId,
destinationId = @destinationId,
relationshipGroup = @relationshipGroup,
typeId = @typeId,
characteristicTypeId = @characteristicTypeId,
modifierId = @modifierId;

-- Text Definition file.
DROP TABLE IF EXISTS textdefinition;
CREATE TABLE textdefinition (
    id NUMERIC(18) UNSIGNED NOT NULL PRIMARY KEY,
    effectiveTime DATE NOT NULL,
    active BOOLEAN NOT NULL,
    moduleId NUMERIC(18) UNSIGNED NOT NULL,
    conceptId NUMERIC(18) UNSIGNED NOT NULL,
    languageCode CHAR(2) NOT NULL,
    typeId NUMERIC(18) UNSIGNED NOT NULL,
    term VARCHAR(2048) NOT NULL,
    caseSignificanceId NUMERIC(18) UNSIGNED NOT NULL,
    FOREIGN KEY (moduleId) REFERENCES concept(id),
    FOREIGN KEY (conceptId) REFERENCES concept(id),
    FOREIGN KEY (typeId) REFERENCES concept(id),
    FOREIGN KEY (caseSignificanceId) REFERENCES concept(id) 
) CHARACTER SET utf8;

LOAD DATA LOCAL INFILE 'C:/Users/perei/Downloads/Terminology/sct2_TextDefinition_Snapshot-en_INT_20140731.txt' INTO TABLE textdefinition LINES TERMINATED BY '\r\n' IGNORE 1 LINES
(@id,@effectiveTime,@active,@moduleId,@conceptId,@languageCode,@typeId,@term,@caseSignificanceId)
SET id = @id,
effectiveTime = @effectiveTime,
active = @active,
moduleId = @moduleId,
conceptId = @conceptId,
languageCode = @languageCode,
typeId = @typeId,
term = @term,
caseSignificanceId = @caseSignificanceId;

-- Restore foreign key checks to enforce referential integrity.
SET FOREIGN_KEY_CHECKS = 1;