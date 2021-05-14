GO

CREATE TRIGGER trigger_atributs_aud_insert
    AFTER INSERT
    ON atributs
    FOR EACH ROW
BEGIN
    IF (SELECT checkEntityIsAudited('ATRIBUTS') = 1) THEN
        INSERT INTO atributs_aud(rev_type, aid, gid, atype, created_by, aval, alocn, aref, adate, created_date, modified_date, modified_by)
        VALUES (0, NEW.aid, NEW.gid, NEW.atype, NEW.created_by, NEW.aval, NEW.alocn, NEW.aref, NEW.adate, NEW.created_date, NEW.modified_date, NEW.modified_by);
    END IF;

END;

GO
