GO

CREATE TRIGGER trigger_bibrefs_aud_delete
    AFTER DELETE
    ON bibrefs
    FOR EACH ROW
BEGIN
    IF (SELECT checkTableIsAudited('BIBREFS') = 1) THEN
        SET @modifiedBy = getAuditModifiedByValue(OLD.modified_by);
        SET @modifiedDate = (SELECT COALESCE(OLD.modified_date, CURRENT_TIMESTAMP));

        INSERT INTO bibrefs_aud(rev_type, refid, pubtype, pubdate, authors, editors, analyt, monogr, series, volume, issue, pagecol, publish, pucity, pucntry, authorlist, editorlist, created_by, modified_by, created_date, modified_date)
        VALUES (2, OLD.refid, OLD.pubtype, OLD.pubdate, OLD.authors, OLD.editors, OLD.analyt, OLD.monogr, OLD.series, OLD.volume, OLD.issue, OLD.pagecol, OLD.publish, OLD.pucity, OLD.pucntry, OLD.authorlist, OLD.editorlist, OLD.created_by, @modifiedBy, OLD.created_date, @modifiedDate);
    END IF;

END;

GO
