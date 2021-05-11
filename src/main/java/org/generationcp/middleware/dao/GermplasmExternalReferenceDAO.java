package org.generationcp.middleware.dao;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmExternalReference;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GermplasmExternalReferenceDAO extends GenericDAO<GermplasmExternalReference, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmExternalReferenceDAO.class);

	private static final String GET_EXTERNAL_REFERENCES =
		"SELECT CAST(gid AS CHAR(255)) as gid, reference_id as referenceID, reference_source referenceSource FROM external_reference WHERE gid IN (:gids)";

	public List<ExternalReferenceDTO> getExternalReferencesByGids(final List<Integer> gids) {

		try {
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(GET_EXTERNAL_REFERENCES);

			sqlQuery.addScalar("gid").addScalar("referenceID").addScalar("referenceSource")
				.setResultTransformer(new AliasToBeanResultTransformer(ExternalReferenceDTO.class));

			sqlQuery.setParameterList("gids", gids);

			return sqlQuery.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getExternalReferencesByGids(gids=" + gids + e.getMessage();
			GermplasmExternalReferenceDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

}
