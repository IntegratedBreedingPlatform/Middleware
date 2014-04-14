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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapLabel;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Season;
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
			criteria.setProjection(Projections.property("experiment.ndExperimentId"));
			
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
            SET @projectId = -139;
            
            SELECT  eproj.project_id AS datasetId  
            , proj.name AS datasetName  
            , geo.nd_geolocation_id AS geolocationId  
            , site.value AS siteName  
            , eproj.nd_experiment_id AS experimentId  
            , s.uniqueName AS entryNumber  
            , s.name AS germplasmName  
            , epropRep.value AS rep  
            , epropPlot.value AS plotNo  
            , row.value AS row  
            , col.value AS col  
            , blk.value AS block_id  
            , inst.description AS trialInstance  
            , st.name AS studyName  
            , s.dbxref_id AS gid  
            , ppStartDate.value as startDate  
            , gpSeason.value as season  
            FROM  nd_experiment_project eproj  
            INNER JOIN project_relationship pr ON pr.object_project_id = :projectId AND pr.type_id = 1150 
            INNER JOIN project st ON st.project_id = pr.object_project_id 
            LEFT JOIN projectprop ppStartDate ON ppStartDate.project_id = pr.object_project_id AND ppStartDate.type_id =  8050  
            INNER JOIN nd_experiment_stock es ON eproj.nd_experiment_id = es.nd_experiment_id AND eproj.project_id = pr.subject_project_id  
            INNER JOIN stock s ON es.stock_id = s.stock_id  
            LEFT JOIN nd_experimentprop epropRep ON eproj.nd_experiment_id = epropRep.nd_experiment_id
            AND epropRep.type_id =  8210  AND eproj.project_id = pr.subject_project_id 
            AND epropRep.value IS NOT NULL  AND epropRep.value <> ''  
            INNER JOIN nd_experimentprop epropPlot ON eproj.nd_experiment_id = epropPlot.nd_experiment_id        
            AND epropPlot.type_id IN (8200, 8380) AND eproj.project_id = pr.subject_project_id        
            AND epropPlot.value IS NOT NULL  AND epropPlot.value <> ''  
            INNER JOIN nd_experiment geo ON eproj.nd_experiment_id = geo.nd_experiment_id AND geo.type_id = 1155 
            INNER JOIN nd_geolocation inst ON geo.nd_geolocation_id = inst.nd_geolocation_id  
            LEFT JOIN nd_geolocationprop site ON geo.nd_geolocation_id = site.nd_geolocation_id AND site.type_id = 8190 
            LEFT JOIN nd_geolocationprop blk ON blk.nd_geolocation_id = geo.nd_geolocation_id AND blk.type_id = 77783 
            INNER JOIN project proj on proj.project_id = eproj.project_id  
            LEFT JOIN nd_experimentprop row ON row.nd_experiment_id = eproj.nd_experiment_id AND row.type_id = 32769 
            LEFT JOIN nd_experimentprop col ON col.nd_experiment_id = eproj.nd_experiment_id AND col.type_id = 32770 
            LEFT JOIN nd_geolocationprop gpSeason ON geo.nd_geolocation_id = gpSeason.nd_geolocation_id AND gpSeason.type_id =  8371  
            ORDER BY eproj.nd_experiment_id DESC
        */
        try {
            String order = projectId > 0 ? "ASC" : "DESC";
            StringBuilder sql = new StringBuilder()
                .append(" SELECT ")
                .append(" eproj.project_id AS datasetId ")
                .append(" , proj.name AS datasetName ")
                .append(" , geo.nd_geolocation_id AS geolocationId ")
                .append(" , site.value AS siteName ")
                .append(" , eproj.nd_experiment_id AS experimentId ")
                .append(" , s.uniqueName AS entryNumber ")
                .append(" , s.name AS germplasmName ")
                .append(" , epropRep.value AS rep ")
                .append(" , epropPlot.value AS plotNo ")
                .append(" , row.value AS row ")
                .append(" , col.value AS col ") 
                .append(" , blk.value AS block_id ")
                //.append("rowsInBlock, cBlock.value AS columnsInBlock, pOrder.value AS plantingOrder, ")
                //.append("rpp.value AS rowsPerPlot, blkName.value AS blockName, ")
                //.append("locName.value AS locationName, fldName.value AS fieldName, ")
                .append(" , inst.description AS trialInstance ")
                .append(" , st.name AS studyName ")
                .append(" , s.dbxref_id AS gid ")
                .append(" , ppStartDate.value as startDate ") 
                .append(" , gpSeason.value as season ")
                .append(" FROM ")
                .append(" nd_experiment_project eproj ")
                .append(" INNER JOIN project_relationship pr ON pr.object_project_id = :projectId AND pr.type_id = ").append(TermId.BELONGS_TO_STUDY.getId())
                .append(" INNER JOIN project st ON st.project_id = pr.object_project_id ")
                .append(" LEFT JOIN projectprop ppStartDate ON ppStartDate.project_id = pr.object_project_id ")
                .append("       AND ppStartDate.type_id =  ").append(TermId.START_DATE.getId()).append(" ") //  8050
                .append(" INNER JOIN nd_experiment_stock es ON eproj.nd_experiment_id = es.nd_experiment_id  ")
                .append("       AND eproj.project_id = pr.subject_project_id ")
                .append(" INNER JOIN stock s ON es.stock_id = s.stock_id ")
                .append(" LEFT JOIN nd_experimentprop epropRep ON eproj.nd_experiment_id = epropRep.nd_experiment_id ")
                .append("       AND epropRep.type_id =  " + TermId.REP_NO.getId()  + "  AND eproj.project_id = pr.subject_project_id ") // 8210
                .append("       AND epropRep.value IS NOT NULL  AND epropRep.value <> '' ")
                .append(" INNER JOIN nd_experimentprop epropPlot ON eproj.nd_experiment_id = epropPlot.nd_experiment_id ")
                .append("       AND epropPlot.type_id IN ("+ TermId.PLOT_NO.getId() + ", "+ TermId.PLOT_NNO.getId() +")  ") //8200, 8380
                .append("       AND eproj.project_id = pr.subject_project_id ")
                .append("       AND epropPlot.value IS NOT NULL  AND epropPlot.value <> '' ")
                .append(" INNER JOIN nd_experiment geo ON eproj.nd_experiment_id = geo.nd_experiment_id ")
                .append("       AND geo.type_id = ").append(TermId.PLOT_EXPERIMENT.getId())
                .append(" INNER JOIN nd_geolocation inst ON geo.nd_geolocation_id = inst.nd_geolocation_id ")
                .append(" LEFT JOIN nd_geolocationprop site ON geo.nd_geolocation_id = site.nd_geolocation_id ")
                .append("       AND site.type_id = ").append(TermId.LOCATION_ID.getId())
                .append(" LEFT JOIN nd_geolocationprop blk ON blk.nd_geolocation_id = geo.nd_geolocation_id ")
                .append("       AND blk.type_id = ").append(TermId.BLOCK_ID.getId())
                .append(" INNER JOIN project proj on proj.project_id = eproj.project_id ")
                .append(" LEFT JOIN nd_experimentprop row ON row.nd_experiment_id = eproj.nd_experiment_id ")
                .append("       AND row.type_id = ").append(TermId.RANGE_NO.getId())
                .append(" LEFT JOIN nd_experimentprop col ON col.nd_experiment_id = eproj.nd_experiment_id ")
                .append("       AND col.type_id = ").append(TermId.COLUMN_NO.getId())
                .append(" LEFT JOIN nd_geolocationprop gpSeason ON geo.nd_geolocation_id = gpSeason.nd_geolocation_id ")
                .append("       AND gpSeason.type_id =  ").append(TermId.SEASON_VAR.getId()).append(" ") //--  8371 (2452) 
                .append(" ORDER BY eproj.nd_experiment_id ").append(order);
                        
            Query query = getSession().createSQLQuery(sql.toString())
                    .addScalar("datasetId")
                    .addScalar("datasetName")
                    .addScalar("geolocationId")
                    .addScalar("siteName")
                    .addScalar("experimentId")
                    .addScalar("entryNumber")
                    .addScalar("germplasmName")
                    .addScalar("rep")
                    .addScalar("plotNo")
                    .addScalar("row")
                    .addScalar("col")
                    .addScalar("block_id")
                    .addScalar("trialInstance")
                    .addScalar("studyName")
                    .addScalar("gid")
                    .addScalar("startDate")
                    .addScalar("season")
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
    
    @SuppressWarnings("unchecked")
    public List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(int datasetId, int geolocationId, Integer blockId) throws MiddlewareQueryException {
        List<FieldMapInfo> fieldmaps = new ArrayList<FieldMapInfo>();

        try {
            String order = geolocationId > 0 ? "ASC" : "DESC";
            StringBuilder sql = new StringBuilder()
                .append(" SELECT ")
                .append(" p.project_id AS datasetId ")
                .append(" , p.name AS datasetName ")
                .append(" , st.name AS studyName ")
                .append(" , e.nd_geolocation_id AS geolocationId ")
                .append(" , site.value AS siteName ")
                .append(" , siteId.value AS siteId")
                .append(" , e.nd_experiment_id AS experimentId ")
                .append(" , s.uniqueName AS entryNumber ")
                .append(" , s.name AS germplasmName ")
                .append(" , epropRep.value AS rep ")
                .append(" , epropPlot.value AS plotNo ")
                .append(" , row.value AS row ")
                .append(" , col.value AS col ")
                .append(" , blk.value AS blockId ")
//                .append(" , rBlock.value AS rowsInBlock ")
//                .append(" , cBlock.value AS columnsInBlock ")
//                .append(" , pOrder.value AS plantingOrder ")
//                .append(" , rpp.value AS rowsPerPlot ")
//                .append(" , blkName.value AS blockName ")
//                .append(" , locName.value AS locationName ")
//                .append(" , fldName.value AS fieldName ")
                .append(" , st.project_id AS studyId ")
//                .append(" , machRow.value AS machineRow ")
                .append(" , geo.description AS trialInstance ")
                .append(" , s.dbxref_id AS gid ")
                .append(" , ppStartDate.value as startDate ") 
                .append(" , gpSeason.value as season ")
                .append(" FROM ")
                .append("  nd_geolocationprop blk ")
                .append("  INNER JOIN nd_experiment e ON e.nd_geolocation_id = blk.nd_geolocation_id ")
                .append("  INNER JOIN nd_geolocation geo ON geo.nd_geolocation_id = e.nd_geolocation_id ")
                .append("  INNER JOIN nd_experiment_project eproj ON eproj.nd_experiment_id = e.nd_experiment_id ")
                .append("  INNER JOIN project p ON p.project_id = eproj.project_id ")
                .append("  INNER JOIN project_relationship pr ON pr.subject_project_id = p.project_id ")
                .append("     AND pr.type_id = ").append(TermId.BELONGS_TO_STUDY.getId())
                .append("  INNER JOIN project st ON st.project_id = pr.object_project_id ")
                .append("  INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id ")
                .append("  INNER JOIN stock s ON es.stock_id = s.stock_id ")
                .append("  LEFT JOIN nd_experimentprop epropRep ON epropRep.nd_experiment_id = e.nd_experiment_id ")
                .append("    AND epropRep.type_id = ").append(TermId.REP_NO.getId()).append(" AND epropRep.value <> '' ")
                .append("  INNER JOIN nd_experimentprop epropPlot ON epropPlot.nd_experiment_id = e.nd_experiment_id ")
                .append("    AND epropPlot.type_id IN (").append(TermId.PLOT_NO.getId()).append(", ")
                            .append(TermId.PLOT_NNO.getId()).append(") ").append(" AND epropPlot.value <> '' ")
                .append("  LEFT JOIN nd_geolocationprop site ON site.nd_geolocation_id = e.nd_geolocation_id ")
                .append("    AND site.type_id = ").append(TermId.TRIAL_LOCATION.getId())
                .append("  LEFT JOIN nd_geolocationprop siteId ON siteId.nd_geolocation_id = e.nd_geolocation_id ")
                .append("    AND siteId.type_id = ").append(TermId.LOCATION_ID.getId())
                .append("  LEFT JOIN nd_experimentprop row ON row.nd_experiment_id = e.nd_experiment_id ")
                .append("    AND row.type_id = ").append(TermId.RANGE_NO.getId())
                .append("  LEFT JOIN nd_experimentprop col ON col.nd_experiment_id = e.nd_experiment_id ")
                .append("    AND col.type_id = ").append(TermId.COLUMN_NO.getId())
//                .append("  LEFT JOIN nd_experimentprop rBlock ON rBlock.nd_experiment_id = uid.nd_experiment_id ")
//                .append("    AND rBlock.type_id = ").append(TermId.COLUMNS_IN_BLOCK.getId())
//                .append("  LEFT JOIN nd_experimentprop cBlock ON cBlock.nd_experiment_id = uid.nd_experiment_id ")
//                .append("    AND cBlock.type_id = ").append(TermId.RANGES_IN_BLOCK.getId())
//                .append("  LEFT JOIN nd_experimentprop pOrder ON pOrder.nd_experiment_id = uid.nd_experiment_id ")
//                .append("    AND pOrder.type_id = ").append(TermId.PLANTING_ORDER.getId())
//                .append("  LEFT JOIN nd_experimentprop rpp ON rpp.nd_experiment_id = uid.nd_experiment_id ")
//                .append("    AND rpp.type_id = ").append(TermId.ROWS_PER_PLOT.getId())
//                .append("  LEFT JOIN nd_experimentprop blkName ON blkName.nd_experiment_id = uid.nd_experiment_id ")
//                .append("    AND blkName.type_id = ").append(TermId.BLOCK_NAME.getId())
//                .append("  LEFT JOIN nd_experimentprop locName ON locName.nd_experiment_id = uid.nd_experiment_id ")
//                .append("    AND locName.type_id = ").append(TermId.SITE_NAME.getId())
//                .append("  LEFT JOIN nd_experimentprop fldName ON fldName.nd_experiment_id = uid.nd_experiment_id ")
//                .append("     AND fldName.type_id = ").append(TermId.FIELD_NAME.getId())
//                .append("  LEFT JOIN nd_experimentprop machRow ON machRow.nd_experiment_id = uid.nd_experiment_id ")
//                .append("     AND machRow.type_id = ").append(TermId.MACHINE_ROW_CAPACITY.getId())
                .append("  LEFT JOIN projectprop ppStartDate ON ppStartDate.project_id = pr.object_project_id ")
                .append("     AND ppStartDate.type_id =  ").append(TermId.START_DATE.getId()).append(" ")
                .append("  LEFT JOIN nd_geolocationprop gpSeason ON geo.nd_geolocation_id = gpSeason.nd_geolocation_id ")
                .append("     AND gpSeason.type_id =  ").append(TermId.SEASON_VAR.getId()).append(" ") //--  8371 (2452)
//                .append(" WHERE uid.value in (SELECT DISTINCT fmid.value ")
//            	.append("    FROM nd_experiment e ")
//                .append("    INNER JOIN nd_experimentprop fmid ON fmid.type_id = 32785 AND fmid.nd_experiment_id = e.nd_experiment_id ")
//                .append("    INNER JOIN nd_experiment_project eproject ON eproject.nd_experiment_id = e.nd_experiment_id ")
//                .append("    WHERE e.nd_geolocation_id = :geolocationId ")
//                .append("    AND eproject.project_id = :datasetId) ")
                .append(" WHERE blk.type_id = ").append(TermId.BLOCK_ID.getId());
            
	            if (blockId != null) {
	            	sql.append(" AND blk.value = :blockId ");
	            }
	            else {
	                sql.append(" AND blk.value IN (SELECT DISTINCT bval.value FROM nd_geolocationprop bval ")
			        .append(" INNER JOIN nd_experiment bexp ON bexp.nd_geolocation_id = bval.nd_geolocation_id ")
				    .append(" AND bexp.nd_geolocation_id = :geolocationId ")
				    .append(" INNER JOIN nd_experiment_project bep ON bep.nd_experiment_id = bexp.nd_experiment_id ")
				    .append(" AND bep.project_id = :datasetId) ");
	            }
	            sql.append(" ORDER BY eproj.nd_experiment_id ").append(order);
	            
                Query query = getSession().createSQLQuery(sql.toString())
                        .addScalar("datasetId")
                        .addScalar("datasetName")
                        .addScalar("studyName")
                        .addScalar("geolocationId")
                        .addScalar("siteName")
                        .addScalar("siteId")
                        .addScalar("experimentId")
                        .addScalar("entryNumber")
                        .addScalar("germplasmName")
                        .addScalar("rep")
                        .addScalar("plotNo")
                        .addScalar("row")
                        .addScalar("col")
                        .addScalar("blockId")
//                        .addScalar("rowsInBlock")
//                        .addScalar("columnsInBlock")
//                        .addScalar("plantingOrder")
//                        .addScalar("rowsPerPlot")
//                        .addScalar("blockName")
//                        .addScalar("locationName")
//                        .addScalar("fieldName")
                        .addScalar("studyId")
//                        .addScalar("machineRow")
                        .addScalar("trialInstance")
                        .addScalar("gid")
                        .addScalar("startDate")
                        .addScalar("season")
                        ;
                
                if (blockId != null) {
                	query.setParameter("blockId", blockId);
                }
                else {
	                query.setParameter("datasetId", datasetId);
	                query.setParameter("geolocationId", geolocationId);
                }
                
                List<Object[]> list =  query.list();           
                
                if (list != null && list.size() > 0) {
                    fieldmaps = createFieldMapLabels(list);        
                }

        
        } catch(HibernateException e) {
            logAndThrowException("Error at getAllFieldMapsInBlockByTrialInstanceId(" + geolocationId + ")", e);
        }
        
        return fieldmaps;
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
        String trialInstanceNo = null;
        Integer blockId = null;
        //String blockName = null;
        //String locationName = null;
        //String fieldName = null;
        //Integer rowsInBlock = null;
        //Integer columnsInBlock = null;
        //Integer plantingOrder = null;
        //Integer rowsPerPlot = null;
        for (Object[] row : list) {
            if (geolocationId == null){
                trialInstance = new FieldMapTrialInstanceInfo();
                labels = new ArrayList<FieldMapLabel>();
            } else {
                //if trial instance or dataset has changed, add previously saved trial instance
                if (!geolocationId.equals((Integer)row[2]) || !datasetId.equals((Integer)row[0])) {
                    trialInstance.setGeolocationId(geolocationId);
                    trialInstance.setSiteName(siteName);
                    trialInstance.setTrialInstanceNo(trialInstanceNo);
                    trialInstance.setBlockId(blockId);
                    //trialInstance.setBlockName(blockName);
                    //trialInstance.setLocationName(locationName);
                    //trialInstance.setFieldName(fieldName);
                    trialInstance.setFieldMapLabels(labels);
                    //trialInstance.setColumnsInBlock(rowsInBlock);
                    //trialInstance.setRangesInBlock(columnsInBlock);
                    //trialInstance.setRowsPerPlot(rowsPerPlot);
                    if (blockId != null) {
                        trialInstance.setHasFieldMap(true);
                        //if (plantingOrder.equals(TermId.ROW_COLUMN.getId())) {
                        //    trialInstance.setPlantingOrder(1);
                        //}
                        //else {
                        //    trialInstance.setPlantingOrder(2);
                        //}
                    }
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
            Integer gid = (Integer) row[14];
            String startDate = (String) row[15];
            String season = (String) row[16];
            
            FieldMapLabel label = new FieldMapLabel(experimentId 
                                , (entryNumber == null || entryNumber.equals("null") || entryNumber.equals("") 
                                        ? null : Integer.parseInt(entryNumber))
                                , germplasmName
                                , (rep == null || rep.equals("null") ? 1 : Integer.parseInt(rep))
                                , (plotNo == null || plotNo.equals("null") ? 0 : Integer.parseInt(plotNo)));
            if (NumberUtils.isNumber((String) row[9])) {
                label.setColumn(Integer.parseInt((String) row[9]));
            }
            if (NumberUtils.isNumber((String) row[10])) {
                label.setRange(Integer.parseInt((String) row[10]));
            }
            label.setStudyName((String) row[13]);
            label.setGid(gid);
            label.setStartYear(startDate != null && !startDate.equals("null") && startDate.length() > 3 ? startDate.substring(0, 4) : null);
            label.setSeason(Season.getSeason(season));
            labels.add(label);
            
            datasetId = (Integer) row[0];
            datasetName = (String) row[1];
            geolocationId = (Integer) row[2];
            siteName = (String) row[3];
            trialInstanceNo = (String) row[12];
            blockId = row[11] != null ? Integer.valueOf((String) row[11]) : null;
            /*
            blockName = (String) row[15];
            locationName = (String) row[16];
            fieldName = (String) row[17];
            
            String rBlock = (String) row[11];
            String cBlock = (String) row[12];
            String pOrder = (String) row[13];
            String rpp = (String) row[14];
            
            if (NumberUtils.isNumber(rBlock)) {
                rowsInBlock = Integer.parseInt(rBlock);
            }
            if (NumberUtils.isNumber(cBlock)) {
                columnsInBlock = Integer.parseInt(cBlock);
            }
            if (NumberUtils.isNumber(pOrder)) {
                plantingOrder = Integer.parseInt(pOrder);
            }
            if (NumberUtils.isNumber(rpp)) {
                rowsPerPlot = Integer.parseInt(rpp);
            }
            */
        }
        //add last trial instance and dataset
        trialInstance.setGeolocationId(geolocationId);
        trialInstance.setSiteName(siteName);
        trialInstance.setTrialInstanceNo(trialInstanceNo);
        /*
        trialInstance.setBlockName(blockName);
        trialInstance.setLocationName(locationName);
        trialInstance.setFieldName(fieldName);
        trialInstance.setColumnsInBlock(rowsInBlock);
        trialInstance.setRangesInBlock(columnsInBlock);
        trialInstance.setRowsPerPlot(rowsPerPlot);
        */
        trialInstance.setFieldMapLabels(labels);
        
        if (blockId != null) {
            trialInstance.setHasFieldMap(true);
        }
        
        trialInstances.add(trialInstance);
        dataset.setDatasetId(datasetId);
        dataset.setDatasetName(datasetName);
        dataset.setTrialInstances(trialInstances);
        datasets.add(dataset);

        return datasets;
    }
    
    
    private List<FieldMapInfo> createFieldMapLabels(List<Object[]> rows) {
        //List<FieldMapLabel> labels = new ArrayList<FieldMapLabel>();
        List<FieldMapInfo> infos = new ArrayList<FieldMapInfo>();
        
        Map<Integer, FieldMapInfo> infoMap = new HashMap<Integer, FieldMapInfo>();
        Map<Integer, FieldMapDatasetInfo> datasetMap = new HashMap<Integer, FieldMapDatasetInfo>();
        Map<String, FieldMapTrialInstanceInfo> trialMap = new HashMap<String, FieldMapTrialInstanceInfo>();
        
        
        for (Object[] row : rows) {
            FieldMapLabel label = new FieldMapLabel();
            String startDate = (String) row[17];
            label.setStudyName((String) row[2]);
            label.setExperimentId(getIntegerValue(row[6]));
            label.setEntryNumber(getIntegerValue(row[7]));
            label.setRep(getIntegerValue(row[9]));
            label.setPlotNo(getIntegerValue(row[10]));
            label.setColumn(getIntegerValue(row[12]));
            label.setRange(getIntegerValue(row[11]));
            label.setGermplasmName((String) row[8]);
            label.setDatasetId((Integer) row[0]);
            label.setGeolocationId((Integer) row[3]);
            label.setSiteName((String) row[4]);
            label.setGid((Integer) row[16]);
            label.setStartYear(startDate != null && !startDate.equals("null") && startDate.length() > 3 ? startDate.substring(0, 4) : null);
            label.setSeason(Season.getSeason((String) row[18]));

            String trialKey = getTrialKey((Integer) row[0], (Integer) row[3]);
            FieldMapTrialInstanceInfo trial = trialMap.get(trialKey);
            if (trial == null) {
                trial = new FieldMapTrialInstanceInfo();
                trial.setGeolocationId((Integer) row[3]);
                trial.setSiteName((String) row[4]);
                trial.setLocationName((String) row[4]);
                if (row[5] != null && NumberUtils.isNumber((String) row[5])) {
                    trial.setLocationId(Integer.valueOf((String) row[5]));
                }
                if (row[13] != null && NumberUtils.isNumber((String) row[13])) {
                	trial.setBlockId(Integer.valueOf((String) row[13]));
                }
//                trial.setRangesInBlock(getIntegerValue(row[13]));
//                trial.setColumnsInBlock(getIntegerValue(row[12]));
//                trial.setMachineRowCapacity(getIntegerValue(row[20]));
//                trial.setRowsPerPlot(getIntegerValue(row[15]));
                trial.setTrialInstanceNo((String) row[15]);
//                Integer pOrder = getIntegerValue(row[14]);
//                if (pOrder != null) {
//                    trial.setPlantingOrder(TermId.SERPENTINE.getId() == pOrder ? 2 : 1);
//                }
//                trial.setBlockName((String) row[16]);
//                trial.setFieldName((String) row[18]);
//                trial.setLocationName((String) row[17]);
                trialMap.put(trialKey, trial);
            }
            
            FieldMapDatasetInfo dataset = datasetMap.get((Integer) row[0]);
            if (dataset == null) {
                dataset = new FieldMapDatasetInfo();
                dataset.setDatasetId((Integer) row[0]);
                dataset.setDatasetName((String) row[1]);
                datasetMap.put(dataset.getDatasetId(), dataset);
                
                FieldMapInfo study = infoMap.get((Integer) row[14]);
                if (study == null) {
                    study = new FieldMapInfo();
                    study.setFieldbookId((Integer) row[14]);
                    study.setFieldbookName((String) row[2]);
                    infoMap.put(study.getFieldbookId(), study);
                }
                if (study.getDatasets() == null) {
                    study.setDatasets(new ArrayList<FieldMapDatasetInfo>());
                }
                if (study.getDataSet(dataset.getDatasetId()) == null) {
                    study.getDatasets().add(dataset);
                }
            }
            if (dataset.getTrialInstances() == null) {
                dataset.setTrialInstances(new ArrayList<FieldMapTrialInstanceInfo>());
            }
            if (dataset.getTrialInstance(trial.getGeolocationId()) == null) {
                dataset.getTrialInstances().add(trial);
            }

            if (trial.getFieldMapLabels() == null) {
                trial.setFieldMapLabels(new ArrayList<FieldMapLabel>());
            }
            trial.getFieldMapLabels().add(label);
        }
        
        Set<Integer> keys = infoMap.keySet();
        for (Integer key : keys) {
            infos.add(infoMap.get(key));
        }
        return infos;
    }
    
    private String getTrialKey(int datasetId, int trialId) {
        return datasetId + "-" + trialId;
    }
    
    private Integer getIntegerValue(Object obj) {
        Integer value = null;
        if (obj != null) { 
            if (obj instanceof Integer) {
                value = (Integer) obj;
            }
            else if (obj instanceof String && NumberUtils.isNumber((String) obj)) {
                value = Integer.valueOf((String) obj);
            }
        }
        return value; 
    }
    
}
