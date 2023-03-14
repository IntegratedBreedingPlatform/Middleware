package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.SampleExternalReference;
import org.hibernate.Session;

public class SampleExternalReferenceDAO extends GenericExternalReferenceDAO<SampleExternalReference>  {

    public SampleExternalReferenceDAO(final Session session) {
        super(session);
    }

    @Override
    String getIdField() {
        return "sample_id";
    }

    @Override
    String getReferenceTable() {
        return "external_reference_sample";
    }
}
