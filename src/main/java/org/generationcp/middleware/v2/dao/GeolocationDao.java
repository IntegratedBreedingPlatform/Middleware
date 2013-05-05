package org.generationcp.middleware.v2.dao;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.pojos.Geolocation;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class GeolocationDao extends GenericDAO<Geolocation, Integer> {

	
	public Geolocation getParentGeolocation(int projectId) throws MiddlewareQueryException {
		try {
			String sql = "SELECT DISTINCT g.*"
					+ " FROM nd_geolocation g"
					+ " INNER JOIN nd_experiment se ON se.nd_geolocation_id = g.nd_geolocation_id"
					+ " INNER JOIN nd_experiment_project sep ON sep.nd_experiment_id = se.nd_experiment_id"
					+ " INNER JOIN project_relationship pr ON pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
					+ " AND pr.object_project_id = sep.project_id AND pr.subject_project_id = :projectId";
			Query query = getSession().createSQLQuery(sql)
								.addEntity(getPersistentClass())
								.setParameter("projectId", projectId);
			return (Geolocation) query.uniqueResult();
						
		} catch(HibernateException e) {
			logAndThrowException("Error at getParentGeolocation=" + projectId + " at GeolocationDao: " + e.getMessage(), e);
		}
		return null;
	}
}
