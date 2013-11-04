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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.LocationDto;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link Geolocation}.
 * 
 */
public class GeolocationDao extends GenericDAO<Geolocation, Integer> {

	private static final String GET_ALL_ENVIRONMENTS_QUERY = 
		"SELECT DISTINCT gp.nd_geolocation_id as envtId, l.lname AS locationName, prov.lname AS provinceName, " +
		"       c.isoabbr, p.project_id, p.name, gp.value AS locationId " +
		"  FROM nd_geolocationprop gp " +
		" INNER JOIN nd_experiment e on e.nd_geolocation_id = gp.nd_geolocation_id " +
		"       AND e.nd_experiment_id = " +
		"		( " +
		"			SELECT MIN(nd_experiment_id) " +
		"			  FROM nd_experiment min" +
		"			 WHERE min.nd_geolocation_id = gp.nd_geolocation_id" +
		"		) " +
		" INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id" +
		" INNER JOIN project_relationship pr ON (pr.object_project_id = ep.project_id OR pr.subject_project_id = ep.project_id) " +
		"		AND pr.type_id = " + TermId.BELONGS_TO_STUDY.getId() +
		" INNER JOIN project p ON p.project_id = pr.object_project_id " +
		"  LEFT JOIN location l ON l.locid = gp.value " +
		"  LEFT JOIN location prov ON prov.locid = l.snl1id " +
		"  LEFT JOIN cntry c ON c.cntryid = l.cntryid " +
		" WHERE gp.type_id = " + TermId.LOCATION_ID.getId();
	
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

	@SuppressWarnings("unchecked")
	public Set<Geolocation> findInDataSet(int datasetId) throws MiddlewareQueryException {
		Set<Geolocation> locations = new LinkedHashSet<Geolocation>();
		try {
			
			String sql = "SELECT DISTINCT e.nd_geolocation_id"
					+ " FROM nd_experiment e"
					+ " INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id"
					+ " WHERE ep.project_id = :projectId ORDER BY e.nd_geolocation_id";
			Query query = getSession().createSQLQuery(sql)
								.setParameter("projectId", datasetId);
			List<Integer> ids = query.list();
			for (Integer id : ids) {
				locations.add(getById(id));
			}
						
		} catch(HibernateException e) {
			logAndThrowException("Error at findInDataSet=" + datasetId + " at GeolocationDao: " + e.getMessage(), e);
		}
		return locations;
	}

	@SuppressWarnings("unchecked")
	public Geolocation findByDescription(String description) throws MiddlewareQueryException {
		try {
			
			String sql = "SELECT DISTINCT loc.nd_geolocation_id"
					+ " FROM nd_geolocation loc"
					+ " WHERE loc.description = :description";
			Query query = getSession().createSQLQuery(sql)
								.setParameter("description", description);
			List<Integer> ids = query.list();
			if (ids.size() >= 1) {
				return getById(ids.get(0));
			}
						
		} catch(HibernateException e) {
			logAndThrowException("Error at findByDescription=" + description + " at GeolocationDao: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Set<Integer> getLocationIds(Integer projectId) throws MiddlewareQueryException {
		Set<Integer> locationIds = new HashSet<Integer>();
		try {
			String sql = "SELECT DISTINCT e.nd_geolocation_id"
					+ " FROM nd_experiment e, nd_experiment_project ep "
					+ " WHERE e.nd_experiment_id = ep.nd_experiment_id "
					+ "   and ep.project_id = " + projectId;
			Query query = getSession().createSQLQuery(sql);
			locationIds.addAll((List<Integer>) query.list());
						
		} catch(HibernateException e) {
			logAndThrowException("Error at getLocationIds=" + projectId + " at GeolocationDao: " + e.getMessage(), e);
		}
		return locationIds;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TrialEnvironment> getAllTrialEnvironments() throws MiddlewareQueryException {
		List<TrialEnvironment> environments = new ArrayList<TrialEnvironment>();
		try {
			SQLQuery query = getSession().createSQLQuery(GET_ALL_ENVIRONMENTS_QUERY);
			query.addScalar("envtId");
			query.addScalar("locationName");
			query.addScalar("provinceName");
			query.addScalar("isoabbr");
			query.addScalar("project_id");
			query.addScalar("name");
			query.addScalar("locationId");
			List<Object[]> list = query.list();
			for (Object[] row : list) {
				if (NumberUtils.isNumber((String) row[6])) {
					environments.add(new TrialEnvironment((Integer) row[0], 
										new LocationDto(Integer.valueOf(row[6].toString()), (String) row[1], (String) row[2], (String) row[3]), 
										new StudyReference((Integer) row[4], (String) row[5])));
				} //otherwise it's invalid data and should not be included
			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error at getAllTrialEnvironments at GeolocationDao: " + e.getMessage(), e);
		}
		return environments;
	}
	
	@SuppressWarnings("unchecked")
	public List<TrialEnvironment> getTrialEnvironments(int start, int numOfRows) throws MiddlewareQueryException {
		List<TrialEnvironment> environments = new ArrayList<TrialEnvironment>();
		try {
			SQLQuery query = getSession().createSQLQuery(GET_ALL_ENVIRONMENTS_QUERY);
			query.addScalar("envtId");
			query.addScalar("locationName");
			query.addScalar("provinceName");
			query.addScalar("isoabbr");
			query.addScalar("project_id");
			query.addScalar("name");
			query.addScalar("locationId");
			setStartAndNumOfRows(query, start, numOfRows);
			List<Object[]> list = query.list();
			for (Object[] row : list) {
				if (NumberUtils.isNumber((String) row[6])) {
					environments.add(new TrialEnvironment((Integer) row[0], 
										new LocationDto(Integer.valueOf(row[6].toString()), (String) row[1], (String) row[2], (String) row[3]), 
										new StudyReference((Integer) row[4], (String) row[5])));
				} //otherwise it's invalid data and should not be included
			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error at getTrialEnvironments at GeolocationDao: " + e.getMessage(), e);
		}
		return environments;
	}
	
	public long countAllTrialEnvironments() throws MiddlewareQueryException {
		try {
			String sql = "SELECT COUNT(DISTINCT nd_geolocation_id) "
				+ " FROM nd_geolocationprop WHERE type_id = " + TermId.LOCATION_ID.getId();
			Query query = getSession().createSQLQuery(sql);
			return ((BigInteger) query.uniqueResult()).longValue();
			
		} catch(HibernateException e) {
			logAndThrowException("Error at countAllTrialEnvironments at GeolocationDao: " + e.getMessage(), e);
		}
		
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(List<Integer> environmentIds) throws MiddlewareQueryException {
		List<TrialEnvironmentProperty> properties = new ArrayList<TrialEnvironmentProperty>();
		try {
			// if categorical value, get related cvterm.definition as property value. 
			// Else, get the value as it's stored in nd_geolocationprop
			String sql = "SELECT DISTINCT gp.type_id, cvt.name, cvt.definition, gp.nd_geolocation_id, "
							+ "CASE WHEN (v.name IS NOT NULL AND cvr.cvterm_relationship_id IS NOT NULL) THEN v.definition "
							+ " ELSE gp.value END AS propvalue "
							+ " FROM nd_geolocationprop gp"
							+ " LEFT JOIN cvterm cvt ON gp.type_id = cvt.cvterm_id"
							+ " LEFT JOIN cvterm v ON v.cvterm_id = gp.value"
							+ " LEFT JOIN cvterm_relationship cvr ON cvr.subject_id = gp.type_id AND cvr.type_id = 1190"
							+ " WHERE gp.nd_geolocation_id IN (:environmentIds)"
							+ " ORDER BY gp.type_id, gp.nd_geolocation_id";
			Query query = getSession().createSQLQuery(sql)
							.setParameterList("environmentIds", environmentIds);
			
			int lastId = 0;
			String lastName = new String();
			String lastDescription = new String();
			Map<Integer, String> environmentValuesMap = new HashMap<Integer, String>();
			
			List<Object[]> result = query.list();
			for (Object[] row : result) {
				Integer id = (Integer) row[0];
				
				if (lastId != id.intValue()){
					String name = (String) row[1];
					String description = (String) row[2];
					
					if (lastId != 0){
						properties.add(new TrialEnvironmentProperty(lastId, lastName, lastDescription, environmentValuesMap));
					}
					
					lastId = id;
					lastName = name;
					lastDescription = description;
					environmentValuesMap = new HashMap<Integer, String>();
				}

				environmentValuesMap.put((Integer)row[3], (String) row[4]);
			}
			
			if (lastId != 0){
				properties.add(new TrialEnvironmentProperty(lastId, lastName, lastDescription, environmentValuesMap));
			}
			
			
		} catch(HibernateException e) {
			logAndThrowException("Error at getPropertiesForTrialEnvironments=" + environmentIds + " at GeolocationDao: " + e.getMessage(), e);
		}
		return properties;
	}
	
	
    @SuppressWarnings("unchecked")
    public List<TrialEnvironment> getTrialEnvironmentDetails(Set<Integer> environmentIds) throws MiddlewareQueryException {
        List<TrialEnvironment> environmentDetails = new ArrayList<TrialEnvironment>();
        
        if (environmentIds.size() == 0){
            return environmentDetails;
        }

        try{
            
        	// Get location name, study id and study name
        	String sql = 
		        "SELECT DISTINCT e.nd_geolocation_id, l.lname, l.locid, p.project_id, p.name " 
        		+ "FROM nd_experiment e "
		        + "	INNER JOIN nd_geolocationprop gp ON e.nd_geolocation_id = gp.nd_geolocation_id " 
        		+ "						AND gp.type_id =  " + TermId.LOCATION_ID.getId() 
        		+ " 					AND e.nd_geolocation_id IN (:locationIds) " 		
		        + "	INNER JOIN location l ON l.locid = gp.value "
		        + "	INNER JOIN nd_experiment_project ep ON e.nd_experiment_id = ep.nd_experiment_id "
		        + "	INNER JOIN project_relationship pr ON pr.subject_project_id = ep.project_id AND pr.type_id = " + TermId.BELONGS_TO_STUDY.getId() + " " 
		        + "	INNER JOIN project p ON p.project_id = pr.object_project_id "
        		;
        	
            Query query = getSession().createSQLQuery(sql)
                    .setParameterList("locationIds", environmentIds);

            List<Integer> locIds = new ArrayList<Integer>();
            
            List<Object[]> result = query.list();
            
            for (Object[] row : result) {
                Integer environmentId = (Integer) row[0];
                String locationName = (String) row[1];
                Integer locId = (Integer) row[2];
                Integer studyId = (Integer) row[3];
                String studyName = (String) row[4];
                
                environmentDetails.add(new TrialEnvironment(environmentId
                                                , new LocationDto(locId, locationName)
                                                , new StudyReference(studyId, studyName)));
                locIds.add(locId);
            }
            
            if (locIds.size() > 0) {
            	// Get province and country
	        	sql =
	        			"SELECT DISTINCT l.locid, prov.lname, c.isoabbr "             		
    	        		+ "FROM nd_experiment e "
    			        + "	INNER JOIN nd_geolocationprop gp ON e.nd_geolocation_id = gp.nd_geolocation_id " 
    	        		+ "						AND gp.type_id =  " + TermId.LOCATION_ID.getId() 
    	        		+ " 					AND e.nd_geolocation_id IN (:locationIds) " 		
    			        + "	INNER JOIN location l ON l.locid = gp.value "
	    		        + "	LEFT JOIN location prov ON prov.locid = l.snl1id "
	    		        + "	LEFT JOIN cntry c ON l.cntryid = c.cntryid "
	    		        ;        
	            query = getSession().createSQLQuery(sql)
	                    .setParameterList("locationIds", environmentIds);
	        	
	            result = query.list();
	            
	
	            for (Object[] row : result) {
	                Integer locationId = (Integer) row[0];
	                String provinceName = (String) row[1];
	                String countryName = (String) row[2];
	                
	                for (int i = 0, size = environmentDetails.size(); i < size; i++){
	                	TrialEnvironment env = environmentDetails.get(i);
	                	LocationDto loc = env.getLocation();

	                	if(loc.getId().intValue() == locationId.intValue()){
	                		loc.setProvinceName(provinceName);
	                    	loc.setCountryName(countryName);
	                    	env.setLocation(loc);
	                	}
	                }
	                
	            }
            }

        } catch(HibernateException e) {
            logAndThrowException("Error at getTrialEnvironmentDetails=" + environmentIds + " at GeolocationDao: " + e.getMessage(), e);
        }


        return environmentDetails;
    }
    
    @SuppressWarnings("unchecked")
	public TrialEnvironments getEnvironmentsForTraits(List<Integer> traitIds) throws MiddlewareQueryException {
		TrialEnvironments environments = new TrialEnvironments();
		try {
			String sql = "SELECT DISTINCT gp.nd_geolocation_id as envtId, l.lname as locationName, prov.lname as provinceName, c.isoabbr, p.project_id, p.name, gp.value as locationId"
						+ " FROM project p"
						+ " INNER JOIN project_relationship pr ON pr.object_project_id = p.project_id AND pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
						+ " INNER JOIN nd_experiment_project ep ON (ep.project_id = p.project_id OR ep.project_id = pr.subject_project_id)"
						+ " INNER JOIN nd_experiment e ON e.nd_experiment_id = ep.nd_experiment_id"
						+ " INNER JOIN nd_experiment_phenotype eph ON eph.nd_experiment_id = e.nd_experiment_id"
						+ " INNER JOIN phenotype ph ON eph.phenotype_id = ph.phenotype_id"
						+ " INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id AND gp.type_id = " + TermId.LOCATION_ID.getId()
						+ " LEFT JOIN location l ON l.locid = gp.value"
						+ " LEFT JOIN location prov ON prov.locid = l.snl1id"
						+ " LEFT JOIN cntry c ON c.cntryid = l.cntryid"
						+ " WHERE ph.observable_id IN (:traitIds);"
						;
			SQLQuery query = getSession().createSQLQuery(sql);
			query.addScalar("envtId");
			query.addScalar("locationName");
			query.addScalar("provinceName");
			query.addScalar("isoabbr");
			query.addScalar("project_id");
			query.addScalar("name");
			query.addScalar("locationId");
			query.setParameterList("traitIds", traitIds);
			List<Object[]> list = query.list();
			for (Object[] row : list) {
				if (NumberUtils.isNumber((String) row[6])) {
					environments.add(new TrialEnvironment((Integer) row[0], 
										new LocationDto(Integer.valueOf(row[6].toString()), (String) row[1], (String) row[2], (String) row[3]), 
										new StudyReference((Integer) row[4], (String) row[5])));
				} //otherwise it's invalid data and should not be included
			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error at getEnvironmentForTraits at GeolocationDao: " + e.getMessage(), e);
		}
		return environments;
	}
    
    @SuppressWarnings("unchecked")
	public Integer getLocationIdByProjectNameAndDescription(String projectName, String locationDescription) throws MiddlewareQueryException {
		try {
			String sql = "SELECT DISTINCT e.nd_geolocation_id"
					+ " FROM nd_experiment e, nd_experiment_project ep, project p, nd_geolocation g "
					+ " WHERE e.nd_experiment_id = ep.nd_experiment_id "
					+ "   and ep.project_id = p.project_id "
					+ "   and e.nd_geolocation_id = g.nd_geolocation_id "
					+ "   and p.name = '"+ projectName + "'" 
					+ "   and g.description = '"+ locationDescription + "'";
			Query query = getSession().createSQLQuery(sql);
			List<Integer> list = (List<Integer>) query.list();
			if(list!=null && !list.isEmpty()) {
				return list.get(0);
			}
						
		} catch(HibernateException e) {
			logAndThrowException("Error at getLocationIdByProjectNameAndDescription with project name =" + projectName + " and location description = "+locationDescription+" at GeolocationDao: " + e.getMessage(), e);
		}
		return null;
	}
    
    
   
 
}
