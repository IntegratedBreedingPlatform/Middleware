GO

CREATE TRIGGER trigger_atributs_aud_update
    AFTER UPDATE
    ON atributs
    FOR EACH ROW
BEGIN
    IF (SELECT checkTableIsAudited('ATRIBUTS') = 1) THEN
        INSERT INTO atributs_aud(rev_type, aid, gid, atype, created_by, aval, cval_id, alocn, aref, adate, created_date, modified_date, modified_by)
        VALUES (1, NEW.aid, NEW.gid, NEW.atype, NEW.created_by, NEW.aval, NEW.cval_id, NEW.alocn, NEW.aref, NEW.adate, NEW.created_date, NEW.modified_date, NEW.modified_by);
    END IF;

END;

GO
