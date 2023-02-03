GO

CREATE TRIGGER trigger_phenotype_aud_update
    AFTER UPDATE
    ON phenotype
    FOR EACH ROW
BEGIN
    IF (SELECT checkTableIsAudited('PHENOTYPE') = 1) THEN
        INSERT INTO phenotype_aud(rev_type, phenotype_id, uniquename, name, observable_id, attr_id, value, cvalue_id, status, assay_id, nd_experiment_id, created_date, updated_date, draft_value, draft_cvalue_id, created_by, updated_by)
        VALUES (1, NEW.phenotype_id, NEW.uniquename, NEW.name, NEW.observable_id, NEW.attr_id, NEW.value, NEW.cvalue_id, NEW.status, NEW.assay_id, NEW.nd_experiment_id, NEW.created_date, NEW.updated_date, NEW.draft_value, NEW.draft_cvalue_id, NEW.created_by, NEW.updated_by);
    END IF;

END;

GO
