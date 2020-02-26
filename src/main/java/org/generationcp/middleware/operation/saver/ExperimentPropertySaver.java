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

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapLabel;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.hibernate.SQLQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExperimentPropertySaver {

	private StringBuilder batchExperimentPropInsertSql = new StringBuilder();
	private final HibernateSessionProvider sessionProvider;
	private final DaoFactory daoFactory;

	public ExperimentPropertySaver(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.sessionProvider = sessionProvider;
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
	 */
	private void saveInBatchOrUpdateProperty(final ExperimentModel experiment, final TermId propertyType, final String value, final Map<Integer, List<ExperimentProperty>> experimentPropertyMap, final Map<String, Boolean> projectPropCreatedMap) {
		final ExperimentProperty experimentProperty = this.getExperimentProperty(experiment, propertyType.getId(), experimentPropertyMap);
		if (experimentProperty == null) {
			final String projectPropKey = experiment.getProject().getProjectId()+"-"+ propertyType;
			if(projectPropCreatedMap.get(projectPropKey) == null){
				new ProjectPropertySaver(this.sessionProvider).createProjectPropertyIfNecessary(experiment.getProject(), propertyType, PhenotypicType.TRIAL_DESIGN);
				projectPropCreatedMap.put(projectPropKey, true);
			}
			createBatchInsertForExperimentProp(experiment.getNdExperimentId(), propertyType.getId(), 0, value);
		}
		else{
			experimentProperty.setValue(value);
			this.daoFactory.getExperimentPropertyDao().saveOrUpdate(experimentProperty);
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

	public void saveOrUpdateProperty(final ExperimentModel experiment, final int propertyType, final String value) {
		ExperimentProperty experimentProperty = this.getExperimentProperty(experiment, propertyType);
		if (experimentProperty == null) {
			experimentProperty = new ExperimentProperty();
			experimentProperty.setTypeId(propertyType);
			experimentProperty.setRank(0);
			experimentProperty.setExperiment(experiment);
		}
		experimentProperty.setValue(value);
		this.daoFactory.getExperimentPropertyDao().saveOrUpdate(experimentProperty);
	}

	private ExperimentProperty getExperimentProperty(final ExperimentModel experiment, final int typeId) {
		if (experiment != null && experiment.getProperties() != null) {
			for (final ExperimentProperty property : experiment.getProperties()) {
				if (property.getTypeId().equals(typeId)) {
					return property;
				}
			}
		}
		return null;
	}

	private ExperimentProperty getExperimentProperty(final ExperimentModel experiment, final int typeId, final Map<Integer, List<ExperimentProperty>> experimentPropertyMap) {
		final List<ExperimentProperty> properties = experimentPropertyMap.get(experiment.getNdExperimentId());

		if (experiment != null && properties != null) {
			for (final ExperimentProperty property : properties) {
				if (property.getTypeId().equals(typeId)) {
					return property;
				}
			}
		}
		return null;
	}


	public void saveFieldmapProperties(final List<FieldMapInfo> infos) {

		// create list of all experimentIds that will be used later to load all experiment and its properties at one go
		final List<Integer> experimentIds = createExperimentIdsList(infos);

		// create experimentId wise experiment entity map
		final Map<Integer, ExperimentModel> experimentMap = this.daoFactory.getExperimentDao().filterByColumnValues("ndExperimentId", experimentIds).stream().collect(
			Collectors.toMap(ExperimentModel::getNdExperimentId, e -> e));

		// create experimentId wise experiment properties map
		final Map<Integer, List<ExperimentProperty>> experimentPropertyMap = createExperimentIdWisePropertiesMap(experimentIds);

		final Map<String, Boolean> projectPropCreatedMap = new HashMap<>();
		for (final FieldMapInfo info : infos) {
			for (final FieldMapDatasetInfo dataset : info.getDatasets()) {
				for (final FieldMapTrialInstanceInfo instanceInfo : dataset.getTrialInstances()) {
					// Save BLOCK_ID at environment level
					if (instanceInfo.getBlockId() != null) {
						this.saveOrUpdateEnvironmentProperty(instanceInfo.getEnvironmentId(), TermId.BLOCK_ID.getId(), instanceInfo.getBlockId().toString());
					}

					if (instanceInfo.getFieldMapLabels() != null) {
						for (final FieldMapLabel label : instanceInfo.getFieldMapLabels()) {
							if (label.getColumn() != null && label.getRange() != null) {
								final ExperimentModel experiment = experimentMap.get(label.getExperimentId());
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
			final SQLQuery sqlQuery = this.sessionProvider.getSession().createSQLQuery(batchExperimentPropInsertSql.toString());
			sqlQuery.executeUpdate();
		}

	}

	private List<Integer> createExperimentIdsList(final List<FieldMapInfo> infos){
		final List<Integer> experimentIds = new ArrayList<>();

		for (final FieldMapInfo info : infos) {
			for (final FieldMapDatasetInfo dataset : info.getDatasets()) {
				for (final FieldMapTrialInstanceInfo tInfo : dataset.getTrialInstances()) {
					if (tInfo.getFieldMapLabels() != null) {
						for (final FieldMapLabel label : tInfo.getFieldMapLabels()) {
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
	 * This method will load experiment properties for all experimentIds and put it in map so we do not need to hit DB to load for each
	 *
	 * @param experimentIds experimentIds for which to load experiment properties
	 * @return Map<Integer, List<ExperimentProperty>> experimentId wise experiment properties
	 */
	private Map<Integer, List<ExperimentProperty>> createExperimentIdWisePropertiesMap(final List<Integer> experimentIds){
		final List<ExperimentProperty> experimentProperties = this.daoFactory.getExperimentPropertyDao().filterByColumnValues("experiment.ndExperimentId", experimentIds);
		final Map<Integer, List<ExperimentProperty>> experimentPropertyMap = new HashMap<>();

		if(experimentProperties != null){
			for(final ExperimentProperty experimentProperty : experimentProperties){
				final Integer experimentId = experimentProperty.getExperiment().getNdExperimentId();
				experimentPropertyMap.putIfAbsent(experimentId, new ArrayList<>());
				experimentPropertyMap.get(experimentId).add(experimentProperty);
			}
		}
		return  experimentPropertyMap;
	}

	private void saveOrUpdateEnvironmentProperty(final int environmentId, final int typeId, final String value) {
		final ExperimentModel environment = this.daoFactory.getEnvironmentDao().getById(environmentId);
		final List<ExperimentProperty> experimentProperties = environment.getProperties();

		ExperimentProperty experimentProperty = null;
		if (environment.getProperties() != null && !environment.getProperties().isEmpty()) {
			experimentProperty = this.getExperimentProperty(environment, typeId);
		}
		if (experimentProperty == null) {
			experimentProperty = new ExperimentProperty();
			experimentProperty.setRank(this.getMaxRank(environment.getProperties()));
			experimentProperty.setExperiment(environment);
			experimentProperty.setTypeId(typeId);
		}
		experimentProperty.setValue(value);
		experimentProperties.add(experimentProperty);
		this.daoFactory.getExperimentDao().saveOrUpdate(environment);

	}

	private int getMaxRank(final List<ExperimentProperty> properties) {
		int maxRank = 1;
		if(properties != null){
			for (final ExperimentProperty property : properties) {
				if (property.getRank() >= maxRank) {
					maxRank = property.getRank() + 1;
				}
			}
		}
		return maxRank;
	}
}
