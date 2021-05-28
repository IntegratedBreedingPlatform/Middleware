GO

CREATE TRIGGER trigger_external_reference_germplasm_aud_delete
    AFTER DELETE
    ON external_reference_germplasm
    FOR EACH ROW
BEGIN
    IF (SELECT checkTableIsAudited('EXTERNAL_REFERENCE_GERMPLASM') = 1) THEN
        SET @modifiedBy = getAuditModifiedByValue(OLD.modified_by);
        SET @modifiedDate = (SELECT COALESCE(OLD.modified_date, CURRENT_TIMESTAMP));
    
        INSERT INTO external_reference_germplasm_aud(rev_type, id, gid, reference_id, reference_source, created_by, modified_by, created_date, modified_date)
        VALUES (2, OLD.id, OLD.gid, OLD.reference_id, OLD.reference_source, OLD.created_by, @modifiedBy, OLD.created_date, @modifiedDate);
    END IF;

END;

GO
