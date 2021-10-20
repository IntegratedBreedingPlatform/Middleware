package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.PhenotypeExternalReference;

public class PhenotypeExternalReferenceDAO extends GenericExternalReferenceDAO<PhenotypeExternalReference> {

    @Override
    String getIdField() {
        return "phenotype_id";
    }

    @Override
    String getReferenceTable() {
        return "external_reference_phenotype";
    }

}
