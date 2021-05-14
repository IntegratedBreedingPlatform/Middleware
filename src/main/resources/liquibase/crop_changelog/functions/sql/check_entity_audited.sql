DROP FUNCTION IF EXISTS checkEntityIsAudited;

GO

CREATE FUNCTION checkEntityIsAudited(entityName VARCHAR(255)) RETURNS BOOLEAN
BEGIN
    RETURN (SELECT is_audited FROM audit_cfg WHERE entity_name = entityName);
END;

GO
