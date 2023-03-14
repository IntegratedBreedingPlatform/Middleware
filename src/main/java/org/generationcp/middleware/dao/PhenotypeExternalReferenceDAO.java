package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.PhenotypeExternalReference;
import org.hibernate.Session;

public class PhenotypeExternalReferenceDAO extends GenericExternalReferenceDAO<PhenotypeExternalReference> {

    public PhenotypeExternalReferenceDAO(final Session session) {
        super(session);
    }

    @Override
    String getIdField() {
        return "phenotype_id";
    }

    @Override
    String getReferenceTable() {
        return "external_reference_phenotype";
    }

}
