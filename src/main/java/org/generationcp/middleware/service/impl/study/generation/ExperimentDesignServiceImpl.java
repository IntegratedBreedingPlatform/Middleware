package org.generationcp.middleware.service.impl.study.generation;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
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
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.generationcp.middleware.service.impl.study.ObservationUnitIDGeneratorImpl;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExperimentDesignServiceImpl implements ExperimentDesignService {

	private DaoFactory daoFactory;

	public ExperimentDesignServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public ExperimentDesignServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public void saveExperimentDesign(final CropType crop, final int studyId, final List<MeasurementVariable> variables, final List<ObservationUnitRow> rows) {
		// TODO VALIDATE that a previous design does not exist for study. Do not continue if so

		// TODO: create stocks if necessary

		final List<Geolocation> geolocations = this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(studyId);
		final ImmutableMap<String, Geolocation> trialInstanceGeolocationMap =
			Maps.uniqueIndex(geolocations, new Function<Geolocation, String>() {

				@Override
				public String apply(final Geolocation geolocation) {
					return geolocation.getDescription();
				}
			});

		final Integer plotDatasetId = this.getPlotDatasetId(studyId);
		for (final ObservationUnitRow row : rows) {
			final Optional<Geolocation> geolocation = this.getGeolocation(trialInstanceGeolocationMap, row);
			final ExperimentModel experimentModel = this.createExperiment(crop, plotDatasetId, row, ExperimentType.PLOT, geolocation);
			experimentModel.setProperties(this.createTrialDesignExperimentProperties(experimentModel, row, variables));
			this.daoFactory.getExperimentDao().save(experimentModel);
		}

	}

	private Integer getPlotDatasetId(final int studyId) {
		final List<DmsProject> plotDatasets = this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.PLOT_DATA.getId());
		if (CollectionUtils.isEmpty(plotDatasets)) {
			throw new MiddlewareException("Study does not have a plot dataset associated to it");
		}
		return plotDatasets.get(0).getProjectId();
	}

	private Integer getEnvironmentDatasetId(final int studyId) {
		final List<DmsProject> plotDatasets = this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId());
		if (CollectionUtils.isEmpty(plotDatasets)) {
			throw new MiddlewareException("Study does not have a trial environment dataset associated to it");
		}
		return plotDatasets.get(0).getProjectId();
	}

	private Optional<Geolocation> getGeolocation(final ImmutableMap<String, Geolocation> trialInstanceGeolocationMap, final ObservationUnitRow row) {
		final Integer trialInstance = row.getTrialInstance();
		if (trialInstance != null) {
			final Geolocation geolocation = trialInstanceGeolocationMap.get(trialInstance.toString());
			if (geolocation != null) {
				return Optional.of(geolocation);
			}
		}
		return Optional.absent();
	}

	private ExperimentModel createExperiment(final CropType crop, final Integer projectId, final ObservationUnitRow row, final ExperimentType expType, final Optional<Geolocation> geolocation) {
		final ExperimentModel experimentModel = new ExperimentModel();
		final DmsProject project = new DmsProject();
		project.setProjectId(projectId);
		experimentModel.setProject(project);
		experimentModel.setTypeId(expType.getTermId());
		final Integer stockId = row.getStockId();
		if (stockId != null) {
			experimentModel.setStock(this.daoFactory.getStockDao().getById(stockId));
		}

		final Geolocation location = geolocation.isPresent()? geolocation.get() : this.createNewGeoLocation();
		experimentModel.setGeoLocation(location);

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
		final ObservationUnitRow row, final List<MeasurementVariable> variables) {
		final ImmutableMap<String, MeasurementVariable> variableMap = Maps.uniqueIndex(variables, new Function<MeasurementVariable, String>() {
			@Override
			public String apply(final MeasurementVariable variable) {
				return variable.getName();
			}
		});

		final List<ExperimentProperty> experimentProperties = new ArrayList<>();

		for (final Map.Entry<String, ObservationUnitData> rowData : row.getVariables().entrySet()) {
			final String variableName = rowData.getKey();
			final MeasurementVariable measurementVariable = variableMap.get(variableName);
			int rank = 1;
			if (measurementVariable != null && VariableType.EXPERIMENTAL_DESIGN.equals(measurementVariable.getVariableType())) {
				experimentProperties.add(this.createTrialDesignProperty(experimentModel, measurementVariable, rank++));
			}
		}

		return experimentProperties;
	}

	private ExperimentProperty createTrialDesignProperty(final ExperimentModel experimentModel, final MeasurementVariable measurementVariable, final Integer rank) {

		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setTypeId(measurementVariable.getTermId());
		experimentProperty.setValue(measurementVariable.getValue());
		experimentProperty.setRank(rank);

		return experimentProperty;
	}

	@Override
	public void deleteExperimentDesign(final int studyId) {
		// Delete variables and experiments of plot dataset
		final Integer environmentDatasetId = this.getPlotDatasetId(studyId);
		this.daoFactory.getProjectPropertyDAO().deleteProjectVariablesByVariableTypes(environmentDatasetId,
			Collections.singletonList(VariableType.EXPERIMENTAL_DESIGN.getId()));

		// Delete variables and experiments of plot dataset
		final Integer plotDatasetId = this.getPlotDatasetId(studyId);
		this.daoFactory.getProjectPropertyDAO().deleteProjectVariablesByVariableTypes(plotDatasetId,
			Arrays.asList(VariableType.GERMPLASM_DESCRIPTOR.getId(), VariableType.EXPERIMENTAL_DESIGN.getId()));
		this.daoFactory.getExperimentDao().deleteExperimentsForDataset(plotDatasetId);
	}

}
