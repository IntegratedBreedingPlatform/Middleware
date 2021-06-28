package org.generationcp.middleware.dao;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ExperimentExternalReference;
import org.generationcp.middleware.pojos.InstanceExternalReference;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ExperimentExternalReferenceDao extends GenericDAO<ExperimentExternalReference, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(StudyExternalReferenceDao.class);

	public List<ExternalReferenceDTO> getExternalReferences(final List<Integer> experimentIds) {

		try {
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(
				"SELECT CAST(nd_experiment_id AS CHAR(255)) as entityId, reference_id as referenceID, reference_source as referenceSource "
					+ "FROM external_reference_experiment WHERE nd_experiment_id IN (:experimentIds)");

			sqlQuery.addScalar("entityId").addScalar("referenceID").addScalar("referenceSource")
				.setResultTransformer(new AliasToBeanResultTransformer(ExternalReferenceDTO.class));

			sqlQuery.setParameterList("experimentIds", experimentIds);

			return sqlQuery.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getExternalReferences(experimentIds=" + experimentIds + e.getMessage();
			ExperimentExternalReferenceDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

}
