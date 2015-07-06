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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Values;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentPhenotype;
import org.generationcp.middleware.pojos.dms.ExperimentProject;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.ExperimentStock;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.util.DatabaseBroker;

public class ExperimentModelSaver extends Saver {

	public ExperimentModelSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void addExperiment(int projectId, ExperimentType experimentType, Values values) throws MiddlewareQueryException {
		TermId myExperimentType = this.mapExperimentType(experimentType);
		ExperimentModel experimentModel = this.create(projectId, values, myExperimentType);
		this.getExperimentDao().save(experimentModel);
		this.addExperimentProject(experimentModel, projectId);
		this.getPhenotypeSaver().savePhenotypes(experimentModel, values.getVariableList());
	}

	public void addOrUpdateExperiment(int projectId, ExperimentType experimentType, Values values) throws MiddlewareQueryException {
		int experimentId =
				this.getExperimentProjectDao().getExperimentIdByLocationIdStockId(projectId, values.getLocationId(),
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

			ExperimentModel experimentModel = this.create(projectId, values, myExperimentType);
			this.getExperimentDao().save(experimentModel);
			this.addExperimentProject(experimentModel, projectId);
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

	private ExperimentModel create(int projectId, Values values, TermId expType) throws MiddlewareQueryException {
		ExperimentModel experimentModel = new ExperimentModel();

		experimentModel.setNdExperimentId(this.getExperimentDao().getNextId("ndExperimentId"));
		experimentModel.setTypeId(expType.getId());
		experimentModel.setProperties(this.createProperties(experimentModel, values.getVariableList()));

		if (values.getLocationId() == null && values instanceof StudyValues) {
			experimentModel.setGeoLocation(this.createNewGeoLocation());
		} else if (values.getLocationId() != null) {
			experimentModel.setGeoLocation(this.getGeolocationDao().getById(values.getLocationId()));
		}
		if (values.getGermplasmId() != null) {
			experimentModel.setExperimentStocks(new ArrayList<ExperimentStock>());
			experimentModel.getExperimentStocks().add(this.createExperimentStock(experimentModel, values.getGermplasmId()));
		}
		return experimentModel;
	}

	// GCP-8092 Nurseries will always have a unique geolocation, no more concept of shared/common geolocation
	private Geolocation createNewGeoLocation() throws MiddlewareQueryException {
		Geolocation location = new Geolocation();
		location.setDescription("1");
		this.getGeolocationDao().save(location);
		return location;
	}

	private List<ExperimentProperty> createProperties(ExperimentModel experimentModel, VariableList factors)
			throws MiddlewareQueryException {
		if (factors != null && factors.getVariables() != null && !factors.getVariables().isEmpty()) {
			int id = this.getExperimentPropertyDao().getNextId("ndExperimentpropId");
			for (Variable variable : factors.getVariables()) {
				if (TermId.TRIAL_DESIGN_INFO_STORAGE.getId() == variable.getVariableType().getStandardVariable().getStoredIn().getId()) {
					this.addProperty(experimentModel, variable, id++);
				}
			}
		}

		return experimentModel.getProperties();
	}

	private void addProperty(ExperimentModel experimentModel, Variable variable, int id) throws MiddlewareQueryException {
		if (experimentModel.getProperties() == null) {
			experimentModel.setProperties(new ArrayList<ExperimentProperty>());
		}
		ExperimentProperty property = new ExperimentProperty();

		property.setNdExperimentpropId(id);
		property.setExperiment(experimentModel);
		property.setTypeId(variable.getVariableType().getId());
		property.setValue(variable.getValue());
		property.setRank(variable.getVariableType().getRank());

		experimentModel.getProperties().add(property);
	}

	private void addExperimentProject(ExperimentModel experimentModel, int projectId) throws MiddlewareQueryException {
		ExperimentProject exproj = new ExperimentProject();
		exproj.setProjectId(projectId);
		exproj.setExperiment(experimentModel);
		this.getExperimentProjectDao().save(exproj);
	}

	private ExperimentStock createExperimentStock(ExperimentModel experiment, int stockId) throws MiddlewareQueryException {
		ExperimentStock experimentStock = new ExperimentStock();
		experimentStock.setExperimentStockId(this.getExperimentStockDao().getNextId("experimentStockId"));
		experimentStock.setTypeId(TermId.IBDB_STRUCTURE.getId());
		experimentStock.setStock(this.getStockModelBuilder().get(stockId));
		experimentStock.setExperiment(experiment);

		return experimentStock;
	}

	public void setExperimentValue(int experimentId, int variableId, Object value) throws MiddlewareQueryException {
		ExperimentModel experiment = this.getExperimentDao().getById(experimentId);
		StandardVariable stdVariable = this.getStandardVariableBuilder().create(variableId);
		if (experiment != null && stdVariable != null) {

			if (stdVariable.getStoredIn().getId() == TermId.TRIAL_DESIGN_INFO_STORAGE.getId()) {
				this.setExperimentValue(experiment, stdVariable, value);
			} else if (stdVariable.getStoredIn().getId() == TermId.OBSERVATION_VARIATE.getId()) {
				this.setObservationVariateValue(experiment, stdVariable, value);
			} else if (stdVariable.getStoredIn().getId() == TermId.CATEGORICAL_VARIATE.getId()) {
				this.setCategoricalVariateValue(experiment, stdVariable, value);
			}
		}
	}

	private void setObservationVariateValue(ExperimentModel experiment, StandardVariable stdVariable, Object value)
			throws MiddlewareQueryException {
		Phenotype phenotype = this.findPhenotype(experiment, stdVariable);
		if (phenotype != null) {
			phenotype.setValue(value == null ? null : value.toString());
			this.getPhenotypeDao().update(phenotype);
		} else {
			this.addNewPhenotype(experiment, stdVariable, value);
		}
	}

	private void setCategoricalVariateValue(ExperimentModel experiment, StandardVariable stdVariable, Object value)
			throws MiddlewareQueryException {
		Phenotype phenotype = this.findPhenotype(experiment, stdVariable);
		if (phenotype != null) {
			phenotype.setcValue(value == null ? null : Integer.valueOf(value.toString()));
			this.getPhenotypeDao().update(phenotype);
		} else {
			this.addNewCPhenotype(experiment, stdVariable, value);
		}
	}

	private Phenotype findPhenotype(ExperimentModel experiment, StandardVariable variable) {
		List<Phenotype> phenotypes = experiment.getPhenotypes();
		if (phenotypes != null) {
			for (Phenotype phenotype : phenotypes) {
				if (phenotype.getObservableId() == variable.getId()) {
					return phenotype;
				}
			}
		}
		return null;
	}

	private void addNewPhenotype(ExperimentModel experimentModel, StandardVariable stdVariable, Object value)
			throws MiddlewareQueryException {
		if (experimentModel.getPhenotypes() == null) {
			experimentModel.setPhenotypes(new ArrayList<Phenotype>());
		}

		Phenotype phenotype = new Phenotype();
		phenotype.setValue(value == null ? null : value.toString());
		phenotype.setObservableId(stdVariable.getId());
		phenotype.setName(String.valueOf(stdVariable.getId()));
		this.getPhenotypeDao().save(phenotype);

		ExperimentPhenotype experimentPhenotype = new ExperimentPhenotype();
		experimentPhenotype.setExperiment(experimentModel.getNdExperimentId());
		experimentPhenotype.setPhenotype(phenotype.getPhenotypeId());
		this.getExperimentPhenotypeDao().save(experimentPhenotype);

		this.getExperimentDao().refresh(experimentModel);
	}

	private void addNewCPhenotype(ExperimentModel experimentModel, StandardVariable stdVariable, Object value)
			throws MiddlewareQueryException {
		if (experimentModel.getPhenotypes() == null) {
			experimentModel.setPhenotypes(new ArrayList<Phenotype>());
		}

		Phenotype phenotype = new Phenotype();
		phenotype.setcValue(value == null ? null : Integer.valueOf(value.toString()));
		phenotype.setObservableId(stdVariable.getId());
		phenotype.setName(String.valueOf(stdVariable.getId()));
		this.getPhenotypeDao().save(phenotype);

		ExperimentPhenotype experimentPhenotype = new ExperimentPhenotype();
		experimentPhenotype.setExperiment(experimentModel.getNdExperimentId());
		experimentPhenotype.setPhenotype(phenotype.getPhenotypeId());
		this.getExperimentPhenotypeDao().save(experimentPhenotype);

		this.getExperimentDao().refresh(experimentModel);
	}

	private void setExperimentValue(ExperimentModel experiment, StandardVariable variable, Object value) throws MiddlewareQueryException {
		ExperimentProperty property = this.findProperty(experiment, variable);
		if (property != null) {
			property.setValue(value == null ? null : value.toString());
			this.getExperimentPropertyDao().update(property);
		} else {
			this.addNewProperty(experiment, variable, value);
		}
	}

	private ExperimentProperty findProperty(ExperimentModel experiment, StandardVariable variable) {
		List<ExperimentProperty> experimentProperties = experiment.getProperties();
		if (experimentProperties != null) {
			for (ExperimentProperty property : experimentProperties) {
				if (property.getTypeId() == variable.getId()) {
					return property;
				}
			}
		}
		return null;
	}

	private void addNewProperty(ExperimentModel experimentModel, StandardVariable stdVariable, Object value)
			throws MiddlewareQueryException {
		if (experimentModel.getProperties() == null) {
			experimentModel.setProperties(new ArrayList<ExperimentProperty>());
		}
		ExperimentProperty property = new ExperimentProperty();

		property.setNdExperimentpropId(this.getExperimentPropertyDao().getNextId("ndExperimentpropId"));
		property.setExperiment(experimentModel);
		property.setTypeId(stdVariable.getId());
		property.setValue(value == null ? null : value.toString());
		property.setRank(0);

		experimentModel.getProperties().add(property);
		this.getExperimentPropertyDao().save(property);
	}

	public int moveStudyToNewGeolocation(int studyId) throws MiddlewareQueryException {
		if (studyId > 0) {
			throw new MiddlewareQueryException("Can not update central studies");
		}
		List<DatasetReference> datasets = this.getDmsProjectDao().getDatasetNodesByStudyId(studyId);
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(studyId);
		if (datasets != null) {
			for (DatasetReference dataset : datasets) {
				ids.add(dataset.getId());
			}
		}

		Geolocation location = this.getGeolocationSaver().createMinimumGeolocation();
		List<ExperimentModel> experiments = this.getExperimentDao().getExperimentsByProjectIds(ids);
		if (experiments != null && !experiments.isEmpty()) {
			int i = 0;
			for (ExperimentModel experiment : experiments) {
				if (experiment.getGeoLocation().getLocationId().intValue() == 1) {
					experiment.setGeoLocation(location);
					this.getExperimentDao().update(experiment);
				}
				if (i > 0 && i % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
					this.getExperimentDao().flush();
					this.getExperimentDao().clear();
				}
				i++;
			}
		}

		return location.getLocationId();
	}
}
