/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.dao.dms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link GeolocationProperty}.
 * 
 */
public class GeolocationPropertyDao extends GenericDAO<GeolocationProperty, Integer> {
	
	@SuppressWarnings("unchecked")
	public List<Integer> getGeolocationIdsByPropertyTypeAndValue(Integer typeId, String value) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("value", value));
			criteria.setProjection(Projections.property("geolocation.locationId"));
			
			return criteria.list();
			
		} catch (HibernateException e) {
			logAndThrowException("Error at getIdsByPropertyTypeAndValue=" + typeId + ", " + value + " query on GeolocationDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}
	
    @SuppressWarnings("unchecked")
    public List<TrialEnvironment> getEnvironmentTraits(Set<TrialEnvironment> trialEnvironments) throws MiddlewareQueryException {
        List<TrialEnvironment> environmentDetails = new ArrayList<TrialEnvironment>();
        
        List<Integer> environmentIds = new ArrayList<Integer>();
        for (TrialEnvironment environment : trialEnvironments) {
            environmentIds.add(environment.getId());
            environmentDetails.add(environment);
        }

        String sql =
                "SELECT nd_geolocation_id, gp.type_id, c.name, c.definition "
                + "FROM nd_geolocationprop gp  "
                + "LEFT JOIN cvterm c ON gp.type_id = c.cvterm_id "
                + "WHERE nd_geolocation_id IN (:locationIds) ";

        try {
            Query query = getSession().createSQLQuery(sql)
                    .setParameterList("locationIds", environmentIds);

            List<Object[]> result = query.list();

            for (Object[] row : result) {
                Integer environmentId = (Integer) row[0];
                Integer traitId = (Integer) row[1];
                String traitName = (String) row[2];
                String traitDescription = (String) row[3];

                int index = environmentDetails.indexOf(new TrialEnvironment(environmentId));
                TrialEnvironment environment = environmentDetails.get(index);
                environment.addTrait(new TraitInfo(traitId, traitName, traitDescription));
                environmentDetails.set(index, environment);
            }

        } catch (HibernateException e) {
            logAndThrowException(
                    "Error at setEnvironmentTraits() query on GeolocationPropertyDao: "
                            + e.getMessage(), e);
        }

        return environmentDetails;
    }


}
