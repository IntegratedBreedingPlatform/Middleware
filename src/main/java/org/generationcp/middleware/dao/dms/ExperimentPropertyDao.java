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
import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapLabel;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
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
    public List<FieldMapDatasetInfo> getFieldMapLabels(int projectId) throws MiddlewareQueryException{
        List<FieldMapDatasetInfo> datasets = null;
        /*  
            SET @projectId = 5790;
                        
            SELECT eproj.project_id AS datasetId, proj.name AS datasetName, 
            geo.nd_geolocation_id AS geolocationId, site.value AS siteName,
            eproj.nd_experiment_id AS experimentId, s.uniquename AS entryNumber, 
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
            INNER JOIN nd_experiment geo ON eproj.nd_experiment_id = geo.nd_experiment_id
              AND geo.type_id = 1155
            INNER JOIN nd_geolocationprop site ON geo.nd_geolocation_id = site.nd_geolocation_id
              AND site.type_id = 8180
            INNER JOIN project proj on proj.project_id = eproj.project_id
 
            ORDER BY eproj.nd_experiment_id ;  -- ASC /DESC depending on the sign of the id
    
        */
        try {
            String order = projectId > 0 ? "ASC" : "DESC";
            StringBuilder sql = new StringBuilder()
                                        .append("SELECT eproj.project_id AS datasetId, proj.name AS datasetName, ")
                                        .append("geo.nd_geolocation_id AS geolocationId, site.value AS siteName, ")
                                        .append("eproj.nd_experiment_id AS experimentId, s.uniquename AS entryNumber, ") 
                                        .append("s.name AS germplasmName, epropRep.value AS rep, epropPlot.value AS plotNo ")
                                        .append("FROM nd_experiment_project eproj  ")
                    .append("   INNER JOIN project_relationship pr ON pr.object_project_id = :projectId AND pr.type_id = ").append(TermId.BELONGS_TO_STUDY.getId())
                                        .append("       INNER JOIN nd_experiment_stock es ON eproj.nd_experiment_id = es.nd_experiment_id  ")
                                        .append("               AND eproj.project_id = pr.subject_project_id ")
                                        .append("       INNER JOIN stock s ON es.stock_id = s.stock_id ")
                                        .append("       LEFT JOIN nd_experimentprop epropRep ON eproj.nd_experiment_id = epropRep.nd_experiment_id ")
                                        .append("               AND epropRep.type_id =  " + TermId.REP_NO.getId()  + "  AND eproj.project_id = pr.subject_project_id ") // 8210
                                        .append("               AND epropRep.value IS NOT NULL  AND epropRep.value <> '' ")
                                        .append("       INNER JOIN nd_experimentprop epropPlot ON eproj.nd_experiment_id = epropPlot.nd_experiment_id ")
                                        .append("               AND epropPlot.type_id IN ("+ TermId.PLOT_NO.getId() + ", "+ TermId.PLOT_NNO.getId() +")  ") //8200, 8380
                                        .append("               AND eproj.project_id = pr.subject_project_id ")
                                        .append("               AND epropPlot.value IS NOT NULL  AND epropPlot.value <> '' ")
                                        .append("       INNER JOIN nd_experiment geo ON eproj.nd_experiment_id = geo.nd_experiment_id ")
                                        .append("               AND geo.type_id = ").append(TermId.PLOT_EXPERIMENT.getId())
                                        .append("       INNER JOIN nd_geolocationprop site ON geo.nd_geolocation_id = site.nd_geolocation_id ")
                                        .append("               AND site.type_id = ").append(TermId.TRIAL_LOCATION.getId())
                                        .append("       INNER JOIN project proj on proj.project_id = eproj.project_id ")
                                        .append("ORDER BY eproj.nd_experiment_id ").append(order);
                        
            Query query = getSession().createSQLQuery(sql.toString())
                    .addScalar("datasetId")
                    .addScalar("datasetName")
                    .addScalar("geolocationId")
                    .addScalar("datasetName")
                    .addScalar("experimentId")
                    .addScalar("entryNumber")
                    .addScalar("germplasmName")
                    .addScalar("rep")
                    .addScalar("plotNo")
                    ;
            query.setParameter("projectId", projectId);
    
            List<Object[]> list =  query.list();           
            
            if (list != null && list.size() > 0) {
                datasets = createFieldMapDatasetInfo(list);        
            }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getFieldMapLabels(projectId=" + projectId + ") at ExperimentPropertyDao: " + e.getMessage(), e);
        }
        
        return datasets;
    }
    
    private List<FieldMapDatasetInfo> createFieldMapDatasetInfo(List<Object[]> list) {
        List<FieldMapDatasetInfo> datasets = new ArrayList<FieldMapDatasetInfo>();
        FieldMapDatasetInfo dataset = null;
        List<FieldMapTrialInstanceInfo> trialInstances = null;
        FieldMapTrialInstanceInfo trialInstance = null;
        List<FieldMapLabel> labels = null;
        Integer datasetId = null;
        Integer geolocationId = null;
        String datasetName = null;
        String siteName = null;
        
        for (Object[] row : list) {
            if (geolocationId == null){
                trialInstance = new FieldMapTrialInstanceInfo();
                labels = new ArrayList<FieldMapLabel>();
            } else {
                //if trial instance or dataset has changed, add previously saved trial instance
                if (!geolocationId.equals((Integer)row[2]) || !datasetId.equals((Integer)row[0])) {
                    trialInstance.setGeolocationId(geolocationId);
                    trialInstance.setSiteName(siteName);
                    trialInstance.setFieldMapLabels(labels);
                    trialInstances.add(trialInstance);
                    trialInstance = new FieldMapTrialInstanceInfo();
                    labels = new ArrayList<FieldMapLabel>();
                }
            }
            
            if (datasetId == null) {
                dataset = new FieldMapDatasetInfo();
                trialInstances = new ArrayList<FieldMapTrialInstanceInfo>();
            } else { 
                //if dataset has changed, add previously saved dataset to the list
                if (!datasetId.equals((Integer)row[0])) {
                    dataset.setDatasetId(datasetId);
                    dataset.setDatasetName(datasetName);
                    dataset.setTrialInstances(trialInstances);
                    datasets.add(dataset);
                    dataset = new FieldMapDatasetInfo();
                    trialInstances = new ArrayList<FieldMapTrialInstanceInfo>();
                } 
            }
            
            Integer experimentId = (Integer) row[4];
            String entryNumber = ((String) row[5]);
            String germplasmName = (String) row[6]; 
            String rep = (String) row[7];
            String plotNo = (String) row[8];

            FieldMapLabel label = new FieldMapLabel(experimentId 
                                , (entryNumber == null ? null : Integer.parseInt(entryNumber))
                                , germplasmName
                                , (rep == null ? 1 : Integer.parseInt(rep))
                                , (plotNo == null ? 0 : Integer.parseInt(plotNo)));
            labels.add(label);
            
            datasetId = (Integer) row[0];
            datasetName = (String) row[1];
            geolocationId = (Integer) row[2];
            siteName = (String) row[3];
        }
        //add last trial instance and dataset
        trialInstance.setGeolocationId(geolocationId);
        trialInstance.setSiteName(siteName);
        trialInstance.setFieldMapLabels(labels);
        trialInstances.add(trialInstance);
        dataset.setDatasetId(datasetId);
        dataset.setDatasetName(datasetName);
        dataset.setTrialInstances(trialInstances);
        datasets.add(dataset);
        
        return datasets;
    }
    
}
