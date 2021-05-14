GO

CREATE TRIGGER trigger_progntrs_aud_update
    AFTER UPDATE
    ON progntrs
    FOR EACH ROW
BEGIN
    IF (SELECT checkEntityIsAudited('PROGENITOR') = 1) THEN
        INSERT INTO progntrs_aud(rev_type, gid, pno, pid, id, created_by, modified_by, created_date, modified_date)
            VALUES (1, NEW.gid, NEW.pno, NEW.pid, NEW.id, NEW.created_by, NEW.modified_by, NEW.created_date, NEW.modified_date);
    END IF;

END;

GO
