GO

CREATE TRIGGER trigger_atributs_aud_delete
    AFTER DELETE
    ON atributs
    FOR EACH ROW
BEGIN
    IF (SELECT checkEntityIsAudited('ATRIBUTS') = 1) THEN
        SET @modifiedBy = getAuditModifiedByValue(OLD.modified_by);
        SET @modifiedDate = (SELECT COALESCE(OLD.modified_date, CURRENT_TIMESTAMP));
    
        INSERT INTO atributs_aud(rev_type, aid, gid, atype, created_by, aval, alocn, aref, adate, created_date, modified_date, modified_by)
        VALUES (2, OLD.aid, OLD.gid, OLD.atype, OLD.created_by, OLD.aval, OLD.alocn, OLD.aref, OLD.adate, OLD.created_date, @modifiedDate, @modifiedBy);
    END IF;

END;

GO
