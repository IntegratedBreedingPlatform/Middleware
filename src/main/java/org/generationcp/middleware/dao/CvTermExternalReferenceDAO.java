package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.CvTermExternalReference;
import org.hibernate.Session;

public class CvTermExternalReferenceDAO extends GenericExternalReferenceDAO<CvTermExternalReference> {

    public CvTermExternalReferenceDAO(final Session session) {
        super(session);
    }

    @Override
    String getIdField() {
        return "cvterm_id";
    }

    @Override
    String getReferenceTable() {
        return "external_reference_cvterm";
    }
}
