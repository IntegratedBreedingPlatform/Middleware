package org.generationcp.middleware.v2.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.pojos.ExperimentModel;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class ExperimentDao extends GenericDAO<ExperimentModel, Integer> {

	@SuppressWarnings("unchecked")
	public List<Integer> getExperimentIdsByGeolocationIds(Collection<Integer> geolocationIds) throws MiddlewareQueryException {
		try {
			if (geolocationIds != null && geolocationIds.size() > 0) {
				Criteria criteria = getSession().createCriteria(getPersistentClass());
				criteria.add(Restrictions.in("geoLocation.locationId", geolocationIds));
				criteria.setProjection(Projections.property("ndExperimentId"));
				
				return criteria.list();
			}
		} catch (HibernateException e) {
			logAndThrowException("Error at getExperimentIdsByGeolocationIds=" + geolocationIds + " query at ExperimentDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	public long countByTrialEnvironmentAndVariate(int trialEnvironmentId, int variateVariableId) throws MiddlewareQueryException {
		try {
			SQLQuery query = getSession().createSQLQuery("select count(distinct e.nd_experiment_id) " +
                                                         "from nd_experiment e, nd_experiment_phenotype ep, phenotype p " + 
                                                         "where e.nd_experiment_id = ep.nd_experiment_id " +
                                                         "   and ep.phenotype_id = p.phenotype_id " +
                                                         "   and e.nd_geolocation_id = " + trialEnvironmentId +
                                                         "   and p.observable_id = " + variateVariableId);
                 
            return ((BigInteger) query.uniqueResult()).longValue();
            
		} catch (HibernateException e) {
			logAndThrowException("Error at countByTrialEnvironmentAndVariate=" + trialEnvironmentId + ", " + variateVariableId + " query at ExperimentDao: " + e.getMessage(), e);
		}
		return 0;
	}
	
}
