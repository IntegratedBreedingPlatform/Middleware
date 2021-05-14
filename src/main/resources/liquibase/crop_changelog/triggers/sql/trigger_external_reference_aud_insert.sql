GO

CREATE TRIGGER trigger_external_reference_aud_insert
    AFTER INSERT
    ON external_reference
    FOR EACH ROW
BEGIN
    IF (SELECT checkEntityIsAudited('EXTERNAL_REFERENCE') = 1) THEN
        INSERT INTO external_reference_aud(rev_type, id, gid, reference_id, reference_source, created_by, modified_by, created_date, modified_date)
        VALUES (0, NEW.id, NEW.gid, NEW.reference_id, NEW.reference_source, NEW.created_by, NEW.modified_by, NEW.created_date, NEW.modified_date);
    END IF;

END;

GO
