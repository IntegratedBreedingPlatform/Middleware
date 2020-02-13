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

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Values;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.operation.builder.StockModelBuilder;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.ObservationUnitIDGenerator;
import org.generationcp.middleware.service.impl.study.ObservationUnitIDGeneratorImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Transactional
public class ExperimentModelSaver {
	
	private DaoFactory daoFactory;
	private PhenotypeSaver phenotypeSaver;
	private StockModelBuilder stockModelBuilder;

	public ExperimentModelSaver(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.phenotypeSaver = new PhenotypeSaver(sessionProvider);
		this.stockModelBuilder = new StockModelBuilder(sessionProvider);
	}

	public ExperimentModel addExperiment(final CropType crop, final int projectId, final ExperimentType experimentType, final Values values) {
		final ExperimentModel experimentModel = this.create(crop, projectId, values, experimentType);

		this.daoFactory.getExperimentDao().save(experimentModel);
		this.phenotypeSaver.savePhenotypes(experimentModel, values.getVariableList());

		return experimentModel;
	}

	public void addOrUpdateExperiment(final CropType crop, final int projectId, final ExperimentType experimentType, final Values values) {
		// Location id is Experiment ID of Environment-level Experiment
		final int experimentId = values.getLocationId();
		if(experimentId != 0 ) {
			for (final Variable variable : values.getVariableList().getVariables()) {
				final int val = this.daoFactory.getPhenotypeDAO()
						.updatePhenotypesByExperimentIdAndObervableId(experimentId, variable.getVariableType().getId(), variable.getValue());
				if (val == 0) {
					this.phenotypeSaver.save(experimentId, variable);
				}
			}
		} else {
			final ExperimentType type = values instanceof StudyValues ? ExperimentType.STUDY_INFORMATION : experimentType;
			final ExperimentModel experimentModel = this.create(crop, projectId, values, type);

			this.daoFactory.getExperimentDao().save(experimentModel);
			this.phenotypeSaver.savePhenotypes(experimentModel, values.getVariableList());
		}
	}

	private ExperimentModel create(final CropType crop, final int projectId, final Values values, final ExperimentType expType) {
		final ExperimentModel experimentModel = new ExperimentModel();
		final DmsProject project = new DmsProject();
		project.setProjectId(projectId);
		experimentModel.setProject(project);
		experimentModel.setTypeId(expType.getTermId());
		experimentModel.setProperties(this.createTrialDesignExperimentProperties(experimentModel, values.getVariableList()));

		if (values.getLocationId() != null) {
			experimentModel.setParent(new ExperimentModel(values.getLocationId()));
		}

		if (values.getObservationUnitNo() != null) {
			experimentModel.setObservationUnitNo(values.getObservationUnitNo());
		}

		if (values.getGermplasmId() != null) {
			experimentModel.setStock(this.stockModelBuilder.get(values.getGermplasmId()));
		}
		final ObservationUnitIDGenerator observationUnitIDGenerator = new ObservationUnitIDGeneratorImpl();
		observationUnitIDGenerator.generateObservationUnitIds(crop, Collections.singletonList(experimentModel));
		return experimentModel;
	}

	protected List<ExperimentProperty> createTrialDesignExperimentProperties(final ExperimentModel experimentModel, final VariableList factors) {

		final List<ExperimentProperty> experimentProperties = new ArrayList<>();

		if (factors != null && factors.getVariables() != null && !factors.getVariables().isEmpty()) {
			for (final Variable variable : factors.getVariables()) {
				final DMSVariableType var = variable.getVariableType();
				if (TermId.TRIAL_INSTANCE_FACTOR.getId() != var.getId() && (PhenotypicType.TRIAL_DESIGN == var.getRole()
					|| PhenotypicType.TRIAL_ENVIRONMENT == var.getRole())) {
					experimentProperties.add(this.createTrialDesignProperty(experimentModel, variable));
				}
			}
		}

		return experimentProperties;
	}

	protected ExperimentProperty createTrialDesignProperty(final ExperimentModel experimentModel, final Variable variable) {

		final ExperimentProperty experimentProperty = new ExperimentProperty();
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

}
