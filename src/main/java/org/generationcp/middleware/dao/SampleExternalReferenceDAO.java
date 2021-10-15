package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.SampleExternalReference;

public class SampleExternalReferenceDAO extends GenericExternalReferenceDAO<SampleExternalReference>  {

    @Override
    String getIdField() {
        return "sample_id";
    }

    @Override
    String getReferenceTable() {
        return "external_reference_sample";
    }
}
