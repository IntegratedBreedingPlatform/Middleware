DROP FUNCTION IF EXISTS getAuditModifiedByValue;

GO

CREATE FUNCTION getAuditModifiedByValue(modifiedBy INT(11)) RETURNS INT(11)
BEGIN
    RETURN (SELECT COALESCE(modifiedBy, (SELECT user_id FROM user_change WHERE tx_id = (SELECT tx.trx_id
                                                            FROM information_schema.innodb_trx tx
                                                            WHERE tx.trx_mysql_thread_id = connection_id())), 0));
END;

GO
