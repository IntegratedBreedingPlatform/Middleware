/*******************************************************************************
 *
 * Generation Challenge Programme (GCP) Copyright (c) 2012, All Rights Reserved.
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.saver;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Values;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.ExperimentStock;
import org.generationcp.middleware.pojos.dms.Geolocation;

import java.util.ArrayList;
import java.util.List;

public class ExperimentModelSaver extends Saver {

	private static final String P = "P";

	public ExperimentModelSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void addExperiment(int projectId, ExperimentType experimentType, Values values, String cropPrefix) {
		TermId myExperimentType = this.mapExperimentType(experimentType);
		ExperimentModel experimentModel = this.create(projectId, values, myExperimentType, cropPrefix);

		this.getExperimentDao().save(experimentModel);
//		this.addExperimentProject(experimentModel, projectId);
		this.getPhenotypeSaver().savePhenotypes(experimentModel, values.getVariableList());
	}

	public void addOrUpdateExperiment(int projectId, ExperimentType experimentType, Values values, String cropPrefix) {
		int experimentId =
				this.getExperimentDao().getExperimentIdByLocationIdStockId(projectId, values.getLocationId(),
						values.getGermplasmId());

		// update if existing
		Boolean isUpdated = false;
		for (Variable variable : values.getVariableList().getVariables()) {
			int val =
					this.getPhenotypeDao().updatePhenotypesByProjectIdAndLocationId(projectId, values.getLocationId(),
							values.getGermplasmId(), variable.getVariableType().getId(), variable.getValue());

			if (val > 0) {
				isUpdated = true;
			}

			if (experimentId != 0 && val == 0) {
				this.getPhenotypeSaver().save(experimentId, variable);
			}

		}

		if (!isUpdated && experimentId == 0) {
			TermId myExperimentType = null;
			if (values instanceof StudyValues) {
				myExperimentType = TermId.STUDY_EXPERIMENT;
			} else {
				myExperimentType = this.mapExperimentType(experimentType);
			}

			ExperimentModel experimentModel = this.create(projectId, values, myExperimentType, cropPrefix);

			this.getExperimentDao().save(experimentModel);
//			this.addExperimentProject(experimentModel, projectId);
			this.getPhenotypeSaver().savePhenotypes(experimentModel, values.getVariableList());
		}
	}

	private TermId mapExperimentType(ExperimentType experimentType) {
		switch (experimentType) {
			case PLOT:
				return TermId.PLOT_EXPERIMENT;
			case AVERAGE:
				return TermId.AVERAGE_EXPERIMENT;
			case SUMMARY:
				return TermId.SUMMARY_EXPERIMENT;
			case SAMPLE:
				return TermId.SAMPLE_EXPERIMENT;
			case STUDY_INFORMATION:
				return TermId.STUDY_INFORMATION;
			case TRIAL_ENVIRONMENT:
				return TermId.TRIAL_ENVIRONMENT_EXPERIMENT;
		}
		return null;
	}

	private ExperimentModel create(int projectId, Values values, TermId expType, String cropPrefix) {
		ExperimentModel experimentModel = new ExperimentModel();
		final DmsProject project = new DmsProject();
		project.setProjectId(projectId);
		experimentModel.setProject(project);
		experimentModel.setTypeId(expType.getId());
		experimentModel.setProperties(this.createTrialDesignExperimentProperties(experimentModel, values.getVariableList()));

		if (values.getLocationId() == null && values instanceof StudyValues) {
			experimentModel.setGeoLocation(this.createNewGeoLocation());
		} else if (values.getLocationId() != null) {
			experimentModel.setGeoLocation(this.getGeolocationDao().getById(values.getLocationId()));
		}
		if (values.getGermplasmId() != null) {
			experimentModel.setExperimentStocks(new ArrayList<ExperimentStock>());
			experimentModel.getExperimentStocks().add(this.createExperimentStock(experimentModel, values.getGermplasmId()));
		}

		if (!(TermId.TRIAL_ENVIRONMENT_EXPERIMENT.equals(expType) && TermId.STUDY_INFORMATION.equals(expType))) {
			String plotUniqueId = getPlotUniqueId(cropPrefix);

			experimentModel.setPlotId(plotUniqueId);
		}

		return experimentModel;
	}

	private String getPlotUniqueId(String cropPrefix) {
		String plotUniqueId = cropPrefix;
		plotUniqueId = plotUniqueId + P;
		plotUniqueId = plotUniqueId + RandomStringUtils.randomAlphanumeric(8);
		
		return plotUniqueId;
	}

	// GCP-8092 Nurseries will always have a unique geolocation, no more concept of shared/common geolocation
	private Geolocation createNewGeoLocation() {
		Geolocation location = new Geolocation();
		location.setDescription("1");
		this.getGeolocationDao().save(location);
		return location;
	}

	protected List<ExperimentProperty> createTrialDesignExperimentProperties(ExperimentModel experimentModel, VariableList factors) {

		List<ExperimentProperty> experimentProperties = new ArrayList<>();

		if (factors != null && factors.getVariables() != null && !factors.getVariables().isEmpty()) {
			for (Variable variable : factors.getVariables()) {
				if (PhenotypicType.TRIAL_DESIGN == variable.getVariableType().getRole()) {
					experimentProperties.add(createTrialDesignProperty(experimentModel, variable));
				}
			}
		}

		return experimentProperties;
	}

	protected ExperimentProperty createTrialDesignProperty(ExperimentModel experimentModel, Variable variable) {

		ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setTypeId(variable.getVariableType().getId());

		if (variable.getVariableType().getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId()) {
			// If the variable is categorical, the variable's categorical value should be saved as categorical id.
			experimentProperty.setValue(variable.getIdValue());
		} else {
			experimentProperty.setValue(variable.getValue());
		}

		experimentProperty.setRank(variable.getVariableType().getRank());

		return experimentProperty;
	}

	private ExperimentStock createExperimentStock(ExperimentModel experiment, int stockId) {
		ExperimentStock experimentStock = new ExperimentStock();
		experimentStock.setTypeId(TermId.IBDB_STRUCTURE.getId());
		experimentStock.setStock(this.getStockModelBuilder().get(stockId));
		experimentStock.setExperiment(experiment);

		return experimentStock;
	}

	public int moveStudyToNewGeolocation(int studyId) {
		List<DatasetReference> datasets = this.getDmsProjectDao().getDatasetNodesByStudyId(studyId);
		List<Integer> ids = new ArrayList<>();
		ids.add(studyId);
		if (datasets != null) {
			for (DatasetReference dataset : datasets) {
				ids.add(dataset.getId());
			}
		}

		Geolocation location = this.getGeolocationSaver().createMinimumGeolocation();
		List<ExperimentModel> experiments = this.getExperimentDao().getExperimentsByProjectIds(ids);
		if (experiments != null && !experiments.isEmpty()) {
			for (ExperimentModel experiment : experiments) {
				if (experiment.getGeoLocation().getLocationId().intValue() == 1) {
					experiment.setGeoLocation(location);
					this.getExperimentDao().update(experiment);
				}
			}
		}

		return location.getLocationId();
	}
}
