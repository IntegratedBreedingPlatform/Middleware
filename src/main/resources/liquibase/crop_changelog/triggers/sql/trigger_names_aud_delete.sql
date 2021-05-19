GO

CREATE TRIGGER trigger_names_aud_delete
    AFTER DELETE
    ON names
    FOR EACH ROW
BEGIN
    IF (SELECT checkEntityIsAudited('NAMES') = 1) THEN
        SET @modifiedBy = getAuditModifiedByValue(OLD.modified_by);
        SET @modifiedDate = (SELECT COALESCE(OLD.modified_date, CURRENT_TIMESTAMP));

        INSERT INTO names_aud(rev_type, nid, gid, ntype, nstat, created_by, nval, nlocn, ndate, nref, modified_by, created_date, modified_date)
        VALUES (2, OLD.nid, OLD.gid, OLD.ntype, OLD.nstat, OLD.created_by, OLD.nval, OLD.nlocn, OLD.ndate, OLD.nref, @modifiedBy, OLD.created_date, @modifiedDate);
    END IF;

END;

GO
