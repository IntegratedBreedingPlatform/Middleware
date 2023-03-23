
package org.generationcp.middleware.dao.mbdt;

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.mbdt.SelectedMarker;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */
@Transactional
public class SelectedMarkerDAO extends GenericDAO<SelectedMarker, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(SelectedMarkerDAO.class);

	public SelectedMarkerDAO(final Session session) {
		super(session);
	}

	public List<SelectedMarker> getMarkersByProjectAndDatasetID(Integer projectID, Integer datasetID) throws MiddlewareQueryException {
		Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());

		criteria.createAlias("generation", "g").createAlias("g.project", "p").add(Restrictions.eq("g.generationID", datasetID))
				.add(Restrictions.eq("p.projectID", projectID));

		return criteria.list();
	}

	public List<SelectedMarker> getMarkersByGenerationID(Integer generationID) throws MiddlewareQueryException {
		Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());

		criteria.add(Restrictions.eq("generation.generationID", generationID));

		return criteria.list();
	}

	@Override
	public SelectedMarker saveOrUpdate(SelectedMarker entity) throws MiddlewareQueryException {
		try {
			SelectedMarker marker = super.saveOrUpdate(entity);

			return marker;
		} catch (MiddlewareQueryException e) {
			SelectedMarkerDAO.LOG.error("Saving or updating was not successful", e);

			throw e;
		}
	}
}
