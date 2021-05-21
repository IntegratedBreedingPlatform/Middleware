GO

CREATE TRIGGER trigger_bibrefs_aud_update
    AFTER UPDATE
    ON bibrefs
    FOR EACH ROW
BEGIN
    IF (SELECT checkTableIsAudited('BIBREFS') = 1)
    THEN
        INSERT INTO bibrefs_aud(rev_type, refid, pubtype, pubdate, authors, editors, analyt, monogr, series, volume, issue, pagecol, publish, pucity, pucntry, authorlist, editorlist, created_by, modified_by, created_date, modified_date)
        VALUES (1, NEW.refid, NEW.pubtype, NEW.pubdate, NEW.authors, NEW.editors, NEW.analyt, NEW.monogr, NEW.series, NEW.volume, NEW.issue, NEW.pagecol, NEW.publish, NEW.pucity, NEW.pucntry, NEW.authorlist, NEW.editorlist, NEW.created_by, NEW.modified_by, NEW.created_date, NEW.modified_date);
    END IF;

END;

GO
