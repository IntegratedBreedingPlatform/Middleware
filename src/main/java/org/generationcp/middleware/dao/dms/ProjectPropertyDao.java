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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link ProjectProperty}.
 * 
 */
public class ProjectPropertyDao extends GenericDAO<ProjectProperty, Integer> {

	@SuppressWarnings("unchecked")
	public Map<String, Set<Integer>> getStandardVariableIdsByPropertyNames(List<String> propertyNames) 
		throws MiddlewareQueryException{
		Map<String, Set<Integer>> standardVariablesInProjects = new HashMap<String, Set<Integer>>();

		// Store the names in the map in uppercase
		for (int i = 0, size = propertyNames.size(); i < size; i++) {
			propertyNames.set(i, propertyNames.get(i).toUpperCase());
		}

		try {

			if (propertyNames.size() > 0) {
								
				StringBuffer sqlString = new StringBuffer()
					.append("SELECT DISTINCT ppValue.value, ppStdVar.id ")
					.append( "FROM projectprop ppValue  ")
					.append("INNER JOIN (SELECT project_id, value id, rank FROM projectprop WHERE type_id = 1070) AS ppStdVar  " )
					.append("    ON ppValue.project_id = ppStdVar.project_id AND ppValue.type_id != 1060  AND ppValue.rank = ppStdVar.rank ")
					.append("    AND ppValue.value IN (:propertyNames) ")
					;
				SQLQuery query = getSession().createSQLQuery(sqlString.toString());
				query.setParameterList("propertyNames", propertyNames);
				
				List<Object[]> results = (List<Object[]>) query.list();
				
        		Set<Integer> stdVarIds = new HashSet<Integer>();
	            for (Object[] row : results){
	            	String name = ((String) row[0]).trim().toUpperCase();
	            	String stdVarId = (String) row[1];
	            	
	            	if (standardVariablesInProjects.containsKey(name)){
	            		stdVarIds = standardVariablesInProjects.get(name);
	            	} else {
	            		stdVarIds = new HashSet<Integer>();
	            	}
	            	try{
	            		stdVarIds.add(Integer.parseInt(stdVarId));
	            		standardVariablesInProjects.put(name, stdVarIds);
	            	} catch (NumberFormatException e) {
	            		//Ignore
	            	}
	            }
			}
		} catch (HibernateException e) {
			logAndThrowException(
					"Error in getStandardVariableIdsInProjects="	+ propertyNames + " in ProjectPropertyDao: " + e.getMessage(), e);
		}

		return standardVariablesInProjects;
	}

	
	
}
