/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapLabel;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.hibernate.SQLQuery;

public class ExperimentPropertySaver extends Saver {

	private StringBuilder batchExperimentPropInsertSql = new StringBuilder();

	public ExperimentPropertySaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void saveOrUpdateProperty(ExperimentModel experiment, TermId propertyType, String value) throws MiddlewareQueryException {
		ExperimentProperty experimentProperty = this.getExperimentProperty(experiment, propertyType.getId());
		if (experimentProperty == null) {
			this.getProjectPropertySaver().createProjectPropertyIfNecessary(experiment.getProject(), propertyType, PhenotypicType.TRIAL_DESIGN);
			experimentProperty = new ExperimentProperty();
			experimentProperty.setTypeId(propertyType.getId());
			experimentProperty.setRank(0);
			experimentProperty.setExperiment(experiment);
		}
		experimentProperty.setValue(value);
		this.getExperimentPropertyDao().saveOrUpdate(experimentProperty);
	}

	/**
	 * This method will call method to create batch insert script for all experiment properties to be saved instead of saving them one by one
	 * or update experiment property directly.
	 *
	 * we are passing experiment-wise properties in experimentPropertyMap
	 * so it will not hit DB to load experiment properties ony by one.
	 *
	 * we also maintain project property created map (projectPropCreatedMap)
	 * so it will not load/check project property every time for same project property
	 *
	 * @param experiment experiment for which to save experiment property
	 * @param propertyType property type
	 * @param value value of property
	 * @param experimentPropertyMap map of experiment wise all experiment properties
	 * @param projectPropCreatedMap map of project property created or not
	 * @throws MiddlewareQueryException
	 */
	public void saveInBatchOrUpdateProperty(ExperimentModel experiment, TermId propertyType, String value, Map<Integer, List<ExperimentProperty>> experimentPropertyMap, Map<String, Boolean> projectPropCreatedMap) throws MiddlewareQueryException {
		ExperimentProperty experimentProperty = this.getExperimentProperty(experiment, propertyType.getId(), experimentPropertyMap);
		if (experimentProperty == null) {
			String projectPropKey = String.valueOf(experiment.getProject().getProjectId()+"-"+ propertyType);
			if(projectPropCreatedMap.get(projectPropKey) == null){
				this.getProjectPropertySaver().createProjectPropertyIfNecessary(experiment.getProject(), propertyType, PhenotypicType.TRIAL_DESIGN);
				projectPropCreatedMap.put(projectPropKey, true);
			}
			createBatchInsertForExperimentProp(experiment.getNdExperimentId(), propertyType.getId(), 0, value);
		}
		else{
			experimentProperty.setValue(value);
			this.getExperimentPropertyDao().saveOrUpdate(experimentProperty);
		}

	}

	/**
	 * This method will create batch insert script for saving large number of experiment properties as one insert statement
	 * instead of saving them one by one.
	 *
	 * @param experimentId experimentId for which new experiment prop to be save
	 * @param typeId typeId of experiment prop
	 * @param rank rank of experiment prop
	 * @param value value of experiment prop
	 */
	private void createBatchInsertForExperimentProp(final int experimentId, final int typeId, final int rank, final String value){
		if(batchExperimentPropInsertSql.length() == 0){
			batchExperimentPropInsertSql = new StringBuilder();
			batchExperimentPropInsertSql.append("insert into  nd_experimentprop (nd_experiment_id, type_id, value , rank) values ");
			batchExperimentPropInsertSql.append("( ");
			batchExperimentPropInsertSql.append(experimentId).append(" , ");
			batchExperimentPropInsertSql.append(typeId).append(" , ");
			batchExperimentPropInsertSql.append(value).append(" , ");
			batchExperimentPropInsertSql.append(rank);
			batchExperimentPropInsertSql.append(") ");
			return;
		}
		batchExperimentPropInsertSql.append(", ( ");
		batchExperimentPropInsertSql.append(experimentId).append(" , ");
		batchExperimentPropInsertSql.append(typeId).append(" , ");
		batchExperimentPropInsertSql.append(value).append(" , ");
		batchExperimentPropInsertSql.append(rank);
		batchExperimentPropInsertSql.append(") ");
	}

	public void saveOrUpdateProperty(ExperimentModel experiment, int propertyType, String value) throws MiddlewareQueryException {
		ExperimentProperty experimentProperty = this.getExperimentProperty(experiment, propertyType);
		if (experimentProperty == null) {
			experimentProperty = new ExperimentProperty();
			experimentProperty.setTypeId(propertyType);
			experimentProperty.setRank(0);
			experimentProperty.setExperiment(experiment);
		}
		experimentProperty.setValue(value);
		this.getExperimentPropertyDao().saveOrUpdate(experimentProperty);
	}

	private ExperimentProperty getExperimentProperty(ExperimentModel experiment, int typeId) {
		if (experiment != null && experiment.getProperties() != null) {
			for (ExperimentProperty property : experiment.getProperties()) {
				if (property.getTypeId().equals(typeId)) {
					return property;
				}
			}
		}
		return null;
	}

	private ExperimentProperty getExperimentProperty(ExperimentModel experiment, int typeId, Map<Integer, List<ExperimentProperty>> experimentPropertyMap) {
		List<ExperimentProperty> properties = experimentPropertyMap.get(experiment.getNdExperimentId());

		if (experiment != null && properties != null) {
			for (ExperimentProperty property : properties) {
				if (property.getTypeId().equals(typeId)) {
					return property;
				}
			}
		}
		return null;
	}


	public void saveFieldmapProperties(List<FieldMapInfo> infos) throws MiddlewareQueryException {

		// create list of all experimentIds that will be used later to load all experiment and its properties at one go
		List<Integer> experimentIds = createExperimentIdsList(infos);

		// create experimentId wise experiment entity map
		Map<Integer, ExperimentModel> experimentMap = createExperimentIdWiseMap(experimentIds);

		// create experimentId wise experiment properties map
		Map<Integer, List<ExperimentProperty>> experimentPropertyMap = createExperimentIdWisePropertiesMap(experimentIds);

		Map<String, Boolean> projectPropCreatedMap = new HashMap<>();

		for (FieldMapInfo info : infos) {
			for (FieldMapDatasetInfo dataset : info.getDatasets()) {
				for (FieldMapTrialInstanceInfo tInfo : dataset.getTrialInstances()) {
					if (tInfo.getFieldMapLabels() != null) {
						for (FieldMapLabel label : tInfo.getFieldMapLabels()) {
							if (label.getColumn() != null && label.getRange() != null) {
								ExperimentModel experiment = experimentMap.get(label.getExperimentId());
								this.saveInBatchOrUpdateProperty(experiment, TermId.COLUMN_NO, String.valueOf(label.getColumn()), experimentPropertyMap, projectPropCreatedMap);
								this.saveInBatchOrUpdateProperty(experiment, TermId.RANGE_NO, String.valueOf(label.getRange()), experimentPropertyMap, projectPropCreatedMap);
							}
						}
					}
				}
			}
		}

		if(batchExperimentPropInsertSql.length() != 0){
			batchExperimentPropInsertSql.append(";");
			SQLQuery sqlQuery = this.getActiveSession().createSQLQuery(batchExperimentPropInsertSql.toString());
			sqlQuery.executeUpdate();
		}

	}

	private List<Integer> createExperimentIdsList(final List<FieldMapInfo> infos){
		List<Integer> experimentIds = new ArrayList<>();

		for (FieldMapInfo info : infos) {
			for (FieldMapDatasetInfo dataset : info.getDatasets()) {
				for (FieldMapTrialInstanceInfo tInfo : dataset.getTrialInstances()) {
					if (tInfo.getFieldMapLabels() != null) {
						for (FieldMapLabel label : tInfo.getFieldMapLabels()) {
							if (label.getColumn() != null && label.getRange() != null) {
								experimentIds.add(label.getExperimentId());
							}
						}
					}
				}
			}
		}
		return  experimentIds;
	}

	/**
	 * This method will load experiment entity for all experimentIds and put it in map so we do not need to hit DB to load for each
	 *
	 * @param experimentIds experimentIds to load
	 * @return Map<Integer, ExperimentModel> experimentId wise experimentModal entity
	 */
	private Map<Integer, ExperimentModel> createExperimentIdWiseMap(final List<Integer> experimentIds){
		Map<Integer, ExperimentModel> experimentMap = new HashMap<>();
		List<ExperimentModel> experiments = this.getExperimentDao().filterByColumnValues("ndExperimentId", experimentIds);
		if(experiments != null){
			for(ExperimentModel experimentModel : experiments){
				experimentMap.put(experimentModel.getNdExperimentId(), experimentModel);
			}
		}
		return  experimentMap;
	}

	/**
	 * This method will load experiment properties for all experimentIds and put it in map so we do not need to hit DB to load for each
	 *
	 * @param experimentIds experimentIds for which to load experiment properties
	 * @return Map<Integer, List<ExperimentProperty>> experimentId wise experiment properties
	 */
	private Map<Integer, List<ExperimentProperty>> createExperimentIdWisePropertiesMap(final List<Integer> experimentIds){
		List<ExperimentProperty> experimentProperties = this.getExperimentPropertyDao().filterByColumnValues("experiment.ndExperimentId", experimentIds);
		Map<Integer, List<ExperimentProperty>> experimentPropertyMap = new HashMap<>();

		if(experimentProperties != null){
			for(ExperimentProperty experimentProperty : experimentProperties){
				Integer experimentId = experimentProperty.getExperiment().getNdExperimentId();
				if(experimentPropertyMap.get(experimentId) == null){
					experimentPropertyMap.put(experimentId, new ArrayList<ExperimentProperty>());
				}
				experimentPropertyMap.get(experimentId).add(experimentProperty);
			}
		}
		return  experimentPropertyMap;
	}
}
