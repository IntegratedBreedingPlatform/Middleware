GO

CREATE TRIGGER trigger_germplsm_aud_delete
    AFTER DELETE
    ON germplsm
    FOR EACH ROW
BEGIN
    IF (SELECT checkTableIsAudited('GERMPLSM') = 1) THEN
        SET @modifiedBy = getAuditModifiedByValue(OLD.modified_by);
        SET @modifiedDate = (SELECT COALESCE(OLD.modified_date, CURRENT_TIMESTAMP));

        INSERT INTO germplsm_aud(rev_type, gid, methn, gnpgs, gpid1, gpid2, created_by, glocn, gdate, gref, grplce, mgid, cid, sid, gchange, deleted, germplsm_uuid, modified_by, created_date, modified_date)
        VALUES (2, OLD.gid, OLD.methn, OLD.gnpgs, OLD.gpid1, OLD.gpid2, OLD.created_by, OLD.glocn, OLD.gdate, OLD.gref, OLD.grplce, OLD.mgid, OLD.cid, OLD.sid, OLD.gchange, OLD.deleted, OLD.germplsm_uuid, @modifiedBy, OLD.created_date, @modifiedDate);
    END IF;

END;

GO
