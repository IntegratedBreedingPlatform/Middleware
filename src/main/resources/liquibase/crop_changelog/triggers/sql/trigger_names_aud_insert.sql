GO

CREATE TRIGGER trigger_names_aud_insert
    AFTER INSERT
    ON names
    FOR EACH ROW
BEGIN
    IF (SELECT checkEntityIsAudited('NAMES') = 1)
    THEN
        INSERT INTO names_aud(rev_type, nid, gid, ntype, nstat, created_by, nval, nlocn, ndate, nref, modified_by, created_date, modified_date)
        VALUES (0, NEW.nid, NEW.gid, NEW.ntype, NEW.nstat, NEW.created_by, NEW.nval, NEW.nlocn, NEW.ndate, NEW.nref, NEW.modified_by, NEW.created_date, NEW.modified_date);
    END IF;

END;


GO
