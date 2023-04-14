GO

CREATE TRIGGER trigger_phenotype_aud_delete
    AFTER DELETE
    ON phenotype
    FOR EACH ROW
BEGIN
    IF (SELECT checkTableIsAudited('PHENOTYPE') = 1) THEN
        SET @updatedBy = getAuditModifiedByValue(OLD.updated_by);
        SET @updated_date = (SELECT COALESCE(OLD.updated_date, CURRENT_TIMESTAMP));
    
        INSERT INTO phenotype_aud(rev_type, phenotype_id, uniquename, name, observable_id, attr_id, value, cvalue_id, status, assay_id, nd_experiment_id, created_date, updated_date, draft_value, draft_cvalue_id, created_by, updated_by, json_props)
        VALUES (2, OLD.phenotype_id, OLD.uniquename, OLD.name, OLD.observable_id, OLD.attr_id, OLD.value, OLD.cvalue_id, OLD.status, OLD.assay_id, OLD.nd_experiment_id, OLD.created_date, @updated_date, OLD.draft_value, OLD.draft_cvalue_id, OLD.created_by, @updatedBy, OLD.json_props);
    END IF;

END;

GO
