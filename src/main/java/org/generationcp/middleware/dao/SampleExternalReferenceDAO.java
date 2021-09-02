package org.generationcp.middleware.dao;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.SampleExternalReference;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SampleExternalReferenceDAO extends GenericDAO<SampleExternalReference, Integer>  {

    private static final Logger LOG = LoggerFactory.getLogger(SampleExternalReferenceDAO.class);

    public List<ExternalReferenceDTO> getExternalReferences(final List<Integer> sampleIds) {

        try {
            final SQLQuery sqlQuery = this.getSession().createSQLQuery(
                    "SELECT CAST(sample_id AS CHAR(255)) as entityId, reference_id as referenceID, reference_source as referenceSource "
                            + "FROM external_reference_sample WHERE sample_id IN (:sampleIds)");

            sqlQuery.addScalar("entityId").addScalar("referenceID").addScalar("referenceSource")
                    .setResultTransformer(new AliasToBeanResultTransformer(ExternalReferenceDTO.class));

            sqlQuery.setParameterList("sampleIds", sampleIds);

            return sqlQuery.list();
        } catch (final HibernateException e) {
            final String errorMessage = "Error with getExternalReferences(sampleIds=" + sampleIds + e.getMessage();
            SampleExternalReferenceDAO.LOG.error(errorMessage, e);
            throw new MiddlewareQueryException(errorMessage, e);
        }
    }
}
