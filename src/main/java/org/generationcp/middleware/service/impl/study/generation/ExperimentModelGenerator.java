package org.generationcp.middleware.service.impl.study.generation;

import com.google.common.base.Optional;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.ObservationUnitIDGenerator;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.impl.study.ObservationUnitIDGeneratorImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExperimentModelGenerator {

	private static final List<VariableType> EXPT_DESIGN_TYPES =
		Arrays.asList(VariableType.EXPERIMENTAL_DESIGN, VariableType.TREATMENT_FACTOR);

	private DaoFactory daoFactory;
		public ExperimentModelGenerator(final HibernateSessionProvider sessionProvider) {
			this.daoFactory = new DaoFactory(sessionProvider);
	}

	public ExperimentModel generate(final CropType crop, final Integer projectId, final ObservationUnitRow row,
		final ExperimentType expType, final Optional<Geolocation> geolocation, final Map<Integer, MeasurementVariable> variablesMap) {
		final ExperimentModel experimentModel = new ExperimentModel();
		final DmsProject project = new DmsProject();
		project.setProjectId(projectId);
		experimentModel.setProject(project);
		experimentModel.setTypeId(expType.getTermId());

		final Geolocation location = geolocation.isPresent() ? geolocation.get() : this.createNewGeoLocation();
		experimentModel.setGeoLocation(location);

		experimentModel.setProperties(this.createTrialDesignExperimentProperties(experimentModel, row, variablesMap));

		final ObservationUnitIDGenerator observationUnitIDGenerator = new ObservationUnitIDGeneratorImpl();
		observationUnitIDGenerator.generateObservationUnitIds(crop, Collections.singletonList(experimentModel));
		return experimentModel;
	}

	private Geolocation createNewGeoLocation() {
		final Geolocation location = new Geolocation();
		location.setDescription("1");
		this.daoFactory.getGeolocationDao().save(location);
		return location;
	}

	private List<ExperimentProperty> createTrialDesignExperimentProperties(final ExperimentModel experimentModel,
		final ObservationUnitRow row, final Map<Integer, MeasurementVariable> variablesMap) {

		final List<ExperimentProperty> experimentProperties = new ArrayList<>();
		for (final Map.Entry<String, ObservationUnitData> rowData : row.getVariables().entrySet()) {
			final ObservationUnitData unitData = rowData.getValue();
			final Integer variableId = unitData.getVariableId();
			final MeasurementVariable measurementVariable = variablesMap.get(variableId);
			int rank = 1;
			if (measurementVariable != null && ExperimentModelGenerator.EXPT_DESIGN_TYPES.contains(measurementVariable.getVariableType())) {
				experimentProperties.add(this.createTrialDesignProperty(experimentModel, measurementVariable, unitData.getValue(), rank));
				rank++;
			}
		}

		return experimentProperties;
	}

	private ExperimentProperty createTrialDesignProperty(final ExperimentModel experimentModel,
		final MeasurementVariable measurementVariable, final String value, final Integer rank) {

		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setTypeId(measurementVariable.getTermId());
		experimentProperty.setValue(value);
		experimentProperty.setRank(rank);

		return experimentProperty;
	}

}
