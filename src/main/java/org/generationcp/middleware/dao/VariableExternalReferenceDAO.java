package org.generationcp.middleware.dao;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ExperimentExternalReference;
import org.generationcp.middleware.pojos.VariableExternalReference;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VariableExternalReferenceDAO extends GenericDAO<VariableExternalReference, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(VariableExternalReferenceDAO.class);

    public List<ExternalReferenceDTO> getExternalReferences(final List<String> cvtermIds) {

        try {
            final SQLQuery sqlQuery = this.getSession().createSQLQuery(
                    "SELECT CAST(cvterm_id AS CHAR(255)) as entityId, reference_id as referenceID, reference_source as referenceSource "
                            + "FROM external_reference_variable WHERE cvterm_id IN (:cvtermIds)");

            sqlQuery.addScalar("entityId").addScalar("referenceID").addScalar("referenceSource")
                    .setResultTransformer(new AliasToBeanResultTransformer(ExternalReferenceDTO.class));

            sqlQuery.setParameterList("cvtermIds", cvtermIds);

            return sqlQuery.list();
        } catch (final HibernateException e) {
            final String errorMessage = "Error with getExternalReferences(cvtermIds=" + cvtermIds + e.getMessage();
            VariableExternalReferenceDAO.LOG.error(errorMessage, e);
            throw new MiddlewareQueryException(errorMessage, e);
        }
    }

}
