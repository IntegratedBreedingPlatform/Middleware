package org.generationcp.middleware.dao;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.StudyExternalReference;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StudyExternalReferenceDao extends GenericDAO<StudyExternalReference, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(StudyExternalReferenceDao.class);

	public List<ExternalReferenceDTO> getExternalReferences(final List<Integer> studyIds) {

		try {
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(
				"SELECT CAST(study_id AS CHAR(255)) as entityId, reference_id as referenceID, reference_source referenceSource "
					+ "FROM external_reference_study WHERE study_id IN (:studyIds)");

			sqlQuery.addScalar("entityId").addScalar("referenceID").addScalar("referenceSource")
				.setResultTransformer(new AliasToBeanResultTransformer(ExternalReferenceDTO.class));

			sqlQuery.setParameterList("studyIds", studyIds);

			return sqlQuery.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getExternalReferences(studyIds=" + studyIds + e.getMessage();
			StudyExternalReferenceDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

}
