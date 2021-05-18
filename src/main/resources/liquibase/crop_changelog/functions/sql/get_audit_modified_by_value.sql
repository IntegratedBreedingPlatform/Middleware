DROP FUNCTION IF EXISTS getAuditModifiedByValue;

GO

CREATE FUNCTION getAuditModifiedByValue(modifiedBy INT(11)) RETURNS INT(11)
BEGIN
    RETURN (SELECT COALESCE(modifiedBy, (SELECT user_id FROM user_change WHERE tx_id = connection_id()), 0));
END;

GO
