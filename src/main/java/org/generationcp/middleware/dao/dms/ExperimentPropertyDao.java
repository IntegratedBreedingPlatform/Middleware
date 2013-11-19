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

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.fieldbook.FieldMapLabel;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.util.Debug;
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
	public List<FieldMapLabel> getFieldMapLabels(int projectId) throws MiddlewareQueryException{
    	List<FieldMapLabel> labels = new ArrayList<FieldMapLabel>();

        /*  
			SET @projectId = 5790;
			
            SELECT eproj.nd_experiment_id AS experimentId, s.uniquename AS entryNumber,  
              s.name AS germplasmName, epropRep.value AS rep, epropPlot.value AS plotNo 
            FROM nd_experiment_project eproj  
            INNER JOIN project_relationship pr ON pr.object_project_id = @projectId AND pr.type_id = 1150
            INNER JOIN nd_experiment_stock es ON eproj.nd_experiment_id = es.nd_experiment_id 
              AND eproj.project_id = pr.subject_project_id 
            INNER JOIN stock s ON es.stock_id = s.stock_id
            LEFT JOIN nd_experimentprop epropRep ON eproj.nd_experiment_id = epropRep.nd_experiment_id 
              AND epropRep.type_id =  8210 AND eproj.project_id = pr.subject_project_id 
              AND epropRep.value IS NOT NULL  AND epropRep.value <> '' 
            INNER JOIN nd_experimentprop epropPlot ON eproj.nd_experiment_id = epropPlot.nd_experiment_id 
              AND epropPlot.type_id IN (8200, 8380)
              AND eproj.project_id = pr.subject_project_id 
              AND epropPlot.value IS NOT NULL  AND epropPlot.value <> '' 
            ORDER BY eproj.nd_experiment_id ;  -- ASC /DESC depending on the sign of the id

        */
        try {
            String order = projectId > 0 ? "ASC" : "DESC";
			StringBuilder sql = new StringBuilder()
					.append("SELECT eproj.nd_experiment_id AS experimentId, s.uniquename AS entryNumber,  ")
					.append("		 s.name AS germplasmName, epropRep.value AS rep, epropPlot.value AS plotNo ")
					.append("FROM nd_experiment_project eproj  ")
                    .append("   INNER JOIN project_relationship pr ON pr.object_project_id = :projectId AND pr.type_id = ").append(TermId.BELONGS_TO_STUDY.getId())
					.append("	INNER JOIN nd_experiment_stock es ON eproj.nd_experiment_id = es.nd_experiment_id  ")
					.append("		AND eproj.project_id = pr.subject_project_id ")
					.append("	INNER JOIN stock s ON es.stock_id = s.stock_id ")
					.append("	LEFT JOIN nd_experimentprop epropRep ON eproj.nd_experiment_id = epropRep.nd_experiment_id ")
					.append("		AND epropRep.type_id =  " + TermId.REP_NO.getId()  + "  AND eproj.project_id = pr.subject_project_id ") // 8210
					.append("		AND epropRep.value IS NOT NULL  AND epropRep.value <> '' ")
					.append("	INNER JOIN nd_experimentprop epropPlot ON eproj.nd_experiment_id = epropPlot.nd_experiment_id ")
					.append("		AND epropPlot.type_id IN ("+ TermId.PLOT_NO.getId() + ", "+ TermId.PLOT_NNO.getId() +")  ") //8200, 8380
					.append("		AND eproj.project_id = pr.subject_project_id ")
					.append("		AND epropPlot.value IS NOT NULL  AND epropPlot.value <> '' ")
					.append("ORDER BY eproj.nd_experiment_id ").append(order);

			Debug.println(3, sql.toString());
			
            Query query = getSession().createSQLQuery(sql.toString())
                    .addScalar("experimentId")
                    .addScalar("entryNumber")
                    .addScalar("germplasmName")
                    .addScalar("rep")
                    .addScalar("plotNo")
                    ;
            query.setParameter("projectId", projectId);

            List<Object[]> list =  query.list();
            
            if (list != null && list.size() > 0) {
                for (Object[] row : list){
                    Integer experimentId = (Integer) row[0];
                    String entryNumber = ((String) row[1]);
                    String germplasmName = (String) row[2]; 
                    String rep = (String) row[3];
                    String plotNo = (String) row[4];

                    FieldMapLabel label = new FieldMapLabel(experimentId 
                			, (entryNumber == null ? null : Integer.parseInt(entryNumber))
                			, germplasmName
                			, (rep == null ? 1 : Integer.parseInt(rep))
                			, (plotNo == null ? 0 : Integer.parseInt(plotNo)));
                    labels.add(label);
                    label.print(6);
                    
                }
            }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getFieldMapLabels(projectId=" + projectId + ") at ExperimentPropertyDao: " + e.getMessage(), e);
        }
        
        return labels;
        
    }
	
    
    
}
