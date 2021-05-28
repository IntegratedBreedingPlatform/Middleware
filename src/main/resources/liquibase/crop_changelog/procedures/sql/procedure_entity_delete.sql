DROP PROCEDURE IF EXISTS entity_delete;

GO

CREATE PROCEDURE entity_delete(IN userId int, IN deleteSQL varchar(255))
BEGIN
    INSERT INTO user_change(user_id, tx_id) VALUE (userId, (SELECT tx.trx_id
                                                            FROM information_schema.innodb_trx tx
                                                            WHERE tx.trx_mysql_thread_id = connection_id()));
    SET @deleteSQL = deleteSQL;
    PREPARE stmt FROM @deleteSQL;
    EXECUTE stmt;
END;

GO
