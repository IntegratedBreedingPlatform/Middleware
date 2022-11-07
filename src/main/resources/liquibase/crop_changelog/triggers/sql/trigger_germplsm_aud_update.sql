GO

CREATE TRIGGER trigger_germplsm_aud_update
    AFTER UPDATE
    ON germplsm
    FOR EACH ROW
BEGIN
    IF (SELECT checkTableIsAudited('GERMPLSM') = 1) THEN
        INSERT INTO germplsm_aud(rev_type, gid, methn, gnpgs, gpid1, gpid2, created_by, glocn, gdate, gref, grplce, mgid, cid, sid, gchange, deleted, germplsm_uuid, modified_by, created_date, modified_date)
        VALUES (1, NEW.gid, NEW.methn, NEW.gnpgs, NEW.gpid1, NEW.gpid2, NEW.created_by, NEW.glocn, NEW.gdate, NEW.gref, NEW.grplce, NEW.mgid, NEW.cid, NEW.sid, NEW.gchange, NEW.deleted, NEW.germplsm_uuid, NEW.modified_by, NEW.created_date, NEW.modified_date);
    END IF;

END;

GO
