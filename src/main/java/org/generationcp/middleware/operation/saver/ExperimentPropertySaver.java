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
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExperimentPropertySaver extends Saver {

	private final DaoFactory daoFactory;

	public ExperimentPropertySaver(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProvider);
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
	public void saveInBatchOrUpdateProperty(final ExperimentModel experiment, final TermId propertyType, final String value,
		final Map<Integer, List<ExperimentProperty>> experimentPropertyMap, final Map<String, Boolean> projectPropCreatedMap)
		throws MiddlewareQueryException {
		ExperimentProperty experimentProperty = this.getExperimentProperty(experiment, propertyType.getId(), experimentPropertyMap);
		if (experimentProperty == null) {
			final String projectPropKey = experiment.getProject().getProjectId() + "-" + propertyType;
			if(projectPropCreatedMap.get(projectPropKey) == null){
				this.getProjectPropertySaver().createProjectPropertyIfNecessary(experiment.getProject(), propertyType, PhenotypicType.TRIAL_DESIGN);
				projectPropCreatedMap.put(projectPropKey, true);
			}
			experimentProperty = new ExperimentProperty(experiment, value, 0, propertyType.getId());
		}
		else{
			experimentProperty.setValue(value);
		}
		this.daoFactory.getExperimentPropertyDao().saveOrUpdate(experimentProperty);
	}

	private ExperimentProperty getExperimentProperty(final ExperimentModel experiment, final int typeId,
		final Map<Integer, List<ExperimentProperty>> experimentPropertyMap) {
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

	public void saveFieldmapProperties(final List<FieldMapInfo> infos) throws MiddlewareQueryException {

		// create list of all experimentIds that will be used later to load all experiment and its properties at one go
		final List<Integer> experimentIds = createExperimentIdsList(infos);

		// create experimentId wise experiment entity map
		final Map<Integer, ExperimentModel> experimentMap = createExperimentIdWiseMap(experimentIds);

		// create experimentId wise experiment properties map
		final Map<Integer, List<ExperimentProperty>> experimentPropertyMap = createExperimentIdWisePropertiesMap(experimentIds);

		final Map<String, Boolean> projectPropCreatedMap = new HashMap<>();

		for (final FieldMapInfo info : infos) {
			for (final FieldMapDatasetInfo dataset : info.getDatasets()) {
				for (final FieldMapTrialInstanceInfo tInfo : dataset.getTrialInstances()) {
					if (tInfo.getFieldMapLabels() != null) {
						for (final FieldMapLabel label : tInfo.getFieldMapLabels()) {
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
	 * This method will load experiment entity for all experimentIds and put it in map so we do not need to hit DB to load for each
	 *
	 * @param experimentIds experimentIds to load
	 * @return Map<Integer, ExperimentModel> experimentId wise experimentModal entity
	 */
	private Map<Integer, ExperimentModel> createExperimentIdWiseMap(final List<Integer> experimentIds){
		final Map<Integer, ExperimentModel> experimentMap = new HashMap<>();
		final List<ExperimentModel> experiments = this.daoFactory.getExperimentDao().filterByColumnValues("ndExperimentId", experimentIds);
		if(experiments != null){
			for (final ExperimentModel experimentModel : experiments) {
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
		final List<ExperimentProperty> experimentProperties =
			this.daoFactory.getExperimentPropertyDao().filterByColumnValues("experiment.ndExperimentId", experimentIds);
		final Map<Integer, List<ExperimentProperty>> experimentPropertyMap = new HashMap<>();

		if(experimentProperties != null){
			for (final ExperimentProperty experimentProperty : experimentProperties) {
				final Integer experimentId = experimentProperty.getExperiment().getNdExperimentId();
				if(experimentPropertyMap.get(experimentId) == null){
					experimentPropertyMap.put(experimentId, new ArrayList<ExperimentProperty>());
				}
				experimentPropertyMap.get(experimentId).add(experimentProperty);
			}
		}
		return  experimentPropertyMap;
	}
}
