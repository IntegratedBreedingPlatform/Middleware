GO

CREATE TRIGGER trigger_progntrs_aud_delete
    AFTER DELETE
    ON progntrs
    FOR EACH ROW
BEGIN
    IF (SELECT checkEntityIsAudited('PROGNTRS') = 1) THEN
        SET @modifiedBy = getAuditModifiedByValue(OLD.modified_by);
        SET @modifiedDate = (SELECT COALESCE(OLD.modified_date, CURRENT_TIMESTAMP));

        INSERT INTO progntrs_aud(rev_type, gid, pno, pid, id, created_by, modified_by, created_date, modified_date)
        VALUES (2, OLD.gid, OLD.pno, OLD.pid, OLD.id, OLD.created_by, @modifiedBy, OLD.created_date, @modifiedDate);
    END IF;
END;

GO
