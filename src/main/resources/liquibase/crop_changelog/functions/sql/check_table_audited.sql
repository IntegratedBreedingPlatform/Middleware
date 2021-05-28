DROP FUNCTION IF EXISTS checkTableIsAudited;

GO

CREATE FUNCTION checkTableIsAudited(tableName VARCHAR(255)) RETURNS BOOLEAN
BEGIN
    RETURN (SELECT is_audited FROM audit_cfg WHERE table_name = tableName);
END;

GO
