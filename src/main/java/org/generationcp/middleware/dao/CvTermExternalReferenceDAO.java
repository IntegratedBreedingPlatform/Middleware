package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.CvTermExternalReference;

public class CvTermExternalReferenceDAO extends GenericExternalReferenceDAO<CvTermExternalReference> {

    @Override
    String getIdField() {
        return "cvterm_id";
    }

    @Override
    String getReferenceTable() {
        return "external_reference_cvterm";
    }
}
