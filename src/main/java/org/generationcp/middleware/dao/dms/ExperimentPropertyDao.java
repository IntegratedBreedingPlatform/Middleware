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
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link ExperimentProperty}.
 * 
 */
public class ExperimentPropertyDao extends GenericDAO<ExperimentProperty, Integer> {

	@SuppressWarnings("unchecked")
	public List<Integer> getExperimentIdsByPropertyTypeAndValue(Integer typeId, String value) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("value", value));
			criteria.setProjection(Projections.property("ndExperimentId"));
			
			return criteria.list();
			
		} catch (HibernateException e) {
			logAndThrowException("Error at getExperimentIdsByPropertyTypeAndValue=" + typeId + ", " + value + " query at ExperimentDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}
	
	
	@SuppressWarnings("unchecked")
    public List<Integer> getRepsOfProject(int projectId) throws MiddlewareQueryException{
	    
        List<Integer> reps = new ArrayList<Integer>();

        /*
             SELECT eprop.value 
             FROM nd_experiment_project eprojREP 
                INNER JOIN nd_experimentprop eprop ON eprojREP.nd_experiment_id = eprop.nd_experiment_id
                        AND eprop.type_id = 8210 AND eprojREP.project_id = @project_id
                        AND eprop.value IS NOT NULL  AND eprop.value <> ''
         */
        try {
            
            StringBuilder sql = new StringBuilder()
                    .append("SELECT eprop.value ")
                    .append("FROM nd_experiment_project eprojREP ")
                    .append("    INNER JOIN nd_experimentprop eprop ON eprojREP.nd_experiment_id = eprop.nd_experiment_id ")
                    .append("            AND eprop.type_id = " + TermId.REP_NO.getId()  + " AND eprojREP.project_id = :projectId ") // 8210
                    .append("            AND eprop.value IS NOT NULL  AND eprop.value <> '' ")
                    ;

            Query query = getSession().createSQLQuery(sql.toString());
            query.setParameter("projectId", projectId);

            List<String> repsString = query.list();
            
            for (String repString : repsString){
                reps.add(Integer.parseInt(repString));
            }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getReps(projectId=" + projectId + ") at ExperimentPropertyDao: " + e.getMessage(), e);
        }
        
        return reps;
	    
	}
	
    public long getPlotCount(int projectId) throws MiddlewareQueryException{
        
        /*  
            SELECT CASE WHEN type_id = 8200 THEN MAX(CAST(eprop.value AS UNSIGNED))
                    WHEN type_id = 8380  THEN MAX(CAST(eprop.value AS SIGNED)) - 100
                    END AS PLOT_COUNT
            FROM nd_experiment_project eproj 
                INNER JOIN nd_experimentprop eprop ON eproj.nd_experiment_id = eprop.nd_experiment_id
                        AND eprop.type_id IN (8200, 8380) AND eproj.project_id = @projectId
                        AND eprop.value IS NOT NULL  AND eprop.value <> ''
        */
        try {
            StringBuilder sql = new StringBuilder()
                    .append("SELECT CASE WHEN type_id = " + TermId.PLOT_NO.getId() + " THEN MAX(CAST(eprop.value AS UNSIGNED)) ")  // PLOT_NO (8200), PLOT_NNO (8380)
                    .append("        WHEN type_id = " + TermId.PLOT_NNO.getId() + "  THEN MAX(CAST(eprop.value AS SIGNED)) - 100 ")
                    .append("        END AS PLOT_COUNT ")
                    .append("FROM nd_experiment_project eproj ")
                    .append("    INNER JOIN nd_experimentprop eprop ON eproj.nd_experiment_id = eprop.nd_experiment_id ")
                    .append("            AND eprop.type_id IN ("+ TermId.PLOT_NO.getId() + ", "+ TermId.PLOT_NNO.getId() +") AND eproj.project_id = :projectId ") // PLOT_NO (8200), PLOT_NNO (8380)
                    .append("            AND eprop.value IS NOT NULL  AND eprop.value <> '' ")
                    ;

            Query query = getSession().createSQLQuery(sql.toString());
            query.setParameter("projectId", projectId);

            return ((BigInteger) query.uniqueResult()).longValue();
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getPlotCount(projectId=" + projectId + ") at ExperimentPropertyDao: " + e.getMessage(), e);
        }
        
        return 0;
        
    }
	
}
