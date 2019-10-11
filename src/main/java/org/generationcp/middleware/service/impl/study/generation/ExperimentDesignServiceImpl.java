package org.generationcp.middleware.service.impl.study.generation;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ExperimentDesignServiceImpl implements ExperimentDesignService {

	private static final List<Integer> EXPERIMENTAL_DESIGN_VARIABLES = Arrays.asList(TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
		TermId.NUMBER_OF_REPLICATES.getId(), TermId.BLOCK_SIZE.getId(), TermId.BLOCKS_PER_REPLICATE.getId(),
		TermId.PERCENTAGE_OF_REPLICATION.getId(),
		TermId.REPLICATIONS_MAP.getId(), TermId.NO_OF_REPS_IN_COLS.getId(), TermId.NO_OF_ROWS_IN_REPS.getId(),
		TermId.NO_OF_COLS_IN_REPS.getId(), TermId.NO_OF_CROWS_LATINIZE.getId(), TermId.NO_OF_CCOLS_LATINIZE.getId(),
		TermId.NO_OF_CBLKS_LATINIZE.getId(), TermId.EXPT_DESIGN_SOURCE.getId(), TermId.NBLKS.getId(),
		TermId.CHECK_PLAN.getId(), TermId.CHECK_INTERVAL.getId(), TermId.CHECK_START.getId());

	private DaoFactory daoFactory;
	private ExperimentModelGenerator experimentGenerator;

	public ExperimentDesignServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public ExperimentDesignServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.experimentGenerator = new ExperimentModelGenerator(sessionProvider);
	}

	@Override
	public void saveExperimentDesign(final CropType crop, final int studyId, final List<MeasurementVariable> variables,
		final List<ObservationUnitRow> rows) {
		// TODO VALIDATE that a previous design does not exist for study. Do not continue if so

		Preconditions.checkNotNull(crop);
		Preconditions.checkState(!CollectionUtils.isEmpty(variables));
		Preconditions.checkState(!CollectionUtils.isEmpty(rows));

		final Integer plotDatasetId = this.getPlotDatasetId(studyId);
		this.saveVariables(studyId, variables, plotDatasetId);

		// Save experiments and stocks (if applicable) in plot dataset
		this.saveObservationUnitRows(crop, studyId, plotDatasetId, variables, rows);

	}

	@Override
	public Optional<Integer> getExperimentDesignTypeTermId(final int studyId) {
		final Integer environmentDatasetId = this.getEnvironmentDatasetId(studyId);
		final ProjectProperty projectProp = this.daoFactory.getProjectPropertyDAO()
			.getByStandardVariableId(new DmsProject(environmentDatasetId), TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		if (projectProp != null && projectProp.getValue() != null && NumberUtils.isDigits(projectProp.getValue())) {
			return Optional.of(Integer.valueOf(projectProp.getValue()));
		}
		return Optional.absent();
	}

	private void saveVariables(final int studyId, final List<MeasurementVariable> variables, final Integer plotDatasetId) {
		int plotDatasetNextRank = this.daoFactory.getProjectPropertyDAO().getNextRank(plotDatasetId);
		final List<Integer> plotVariableIds = this.daoFactory.getProjectPropertyDAO().getVariableIdsForDataset(plotDatasetId);

		final Integer envDatasetId = this.getEnvironmentDatasetId(studyId);
		int envDatasetNextRank = this.daoFactory.getProjectPropertyDAO().getNextRank(envDatasetId);
		final List<Integer> envVariableIds = this.daoFactory.getProjectPropertyDAO().getVariableIdsForDataset(envDatasetId);
		// Save project variables in environment and plot datasets
		for (final MeasurementVariable variable : variables) {
			final int variableId = variable.getTermId();
			final VariableType variableType = variable.getVariableType();
			final boolean isEnvironmentVariable = VariableType.ENVIRONMENT_DETAIL.equals(variableType);
			if (!this.variableExists(variableId, isEnvironmentVariable, envVariableIds, plotVariableIds)) {
				Integer projectId = plotDatasetId;
				Integer rank = 1;
				Integer variableTypeId = variableType.getId();
				if (isEnvironmentVariable) {
					projectId = envDatasetId;
					rank = envDatasetNextRank++;

				} else {
					// FIXME: Current bug that saves treatment factor as projectprop with type_id = 1100. Should be 1809
					if (VariableType.TREATMENT_FACTOR.equals(variableType)) {
						variableTypeId = TermId.MULTIFACTORIAL_INFO.getId();

						// Save a record for same variable with variable type = EXPERIMENT_DESIGN
						final DmsProject project = new DmsProject();
						project.setProjectId(projectId);
						final ProjectProperty property =
							new ProjectProperty(project, VariableType.EXPERIMENTAL_DESIGN.getId(), "", plotDatasetNextRank++, variableId, variable.getAlias());
						this.daoFactory.getProjectPropertyDAO().save(property);
					}
					rank = plotDatasetNextRank++;
				}

				final DmsProject project = new DmsProject();
				project.setProjectId(projectId);
				final ProjectProperty property =
					new ProjectProperty(project, variableTypeId, variable.getValue(), rank, variableId, variable.getAlias());
				this.daoFactory.getProjectPropertyDAO().save(property);
			}

		}
	}

	private boolean variableExists(final Integer variableId,final Boolean isEnvironmentVariable, final List<Integer> environmentVariableIds, final List<Integer> plotVariableIds) {
		return isEnvironmentVariable ? environmentVariableIds.contains(variableId) : plotVariableIds.contains(variableId);
	}

	private void saveObservationUnitRows(final CropType crop, final Integer studyId, final Integer plotDatasetId,
		final List<MeasurementVariable> variables,
		final List<ObservationUnitRow> rows) {
		final List<Geolocation> geolocations = this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(studyId);
		final ImmutableMap<String, Geolocation> trialInstanceGeolocationMap =
			Maps.uniqueIndex(geolocations, new Function<Geolocation, String>() {

				@Override
				public String apply(final Geolocation geolocation) {
					return geolocation.getDescription();
				}
			});

		final Set<StockModel> stocks = this.daoFactory.getStockDao().findInDataSet(plotDatasetId);
		final ImmutableMap<String, StockModel> stocksMap =
			Maps.uniqueIndex(stocks, new Function<StockModel, String>() {

				@Override
				public String apply(final StockModel stock) {
					return stock.getUniqueName();
				}
			});

		final ImmutableMap<Integer, MeasurementVariable> variablesMap =
			Maps.uniqueIndex(variables, new Function<MeasurementVariable, Integer>() {

				@Override
				public Integer apply(final MeasurementVariable measurementVariable) {
					return measurementVariable.getTermId();
				}
			});

		for (final ObservationUnitRow row : rows) {
			final Optional<Geolocation> geolocation = this.getGeolocation(trialInstanceGeolocationMap, row);
			final ExperimentModel
				experimentModel = this.experimentGenerator.generate(crop, plotDatasetId, row, ExperimentType.PLOT, geolocation, variablesMap);
			final String entryNumber = String.valueOf(row.getEntryNumber());
			StockModel stockModel = stocksMap.get(entryNumber);
			if (stockModel == null) {
				stockModel = new StockModelGenerator().generate(variablesMap, Lists.newArrayList(row.getVariables().values()));
				this.daoFactory.getStockDao().save(stockModel);
			}
			experimentModel.setStock(stockModel);
			this.daoFactory.getExperimentDao().save(experimentModel);
		}
	}

	private Integer getPlotDatasetId(final int studyId) {
		final List<DmsProject> plotDatasets =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.PLOT_DATA.getId());
		if (CollectionUtils.isEmpty(plotDatasets)) {
			throw new MiddlewareException("Study does not have a plot dataset associated to it");
		}
		return plotDatasets.get(0).getProjectId();
	}

	private Integer getEnvironmentDatasetId(final int studyId) {
		final List<DmsProject> plotDatasets =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId());
		if (CollectionUtils.isEmpty(plotDatasets)) {
			throw new MiddlewareException("Study does not have a trial environment dataset associated to it");
		}
		return plotDatasets.get(0).getProjectId();
	}

	private Optional<Geolocation> getGeolocation(final ImmutableMap<String, Geolocation> trialInstanceGeolocationMap,
		final ObservationUnitRow row) {
		final Integer trialInstance = row.getTrialInstance();
		if (trialInstance != null) {
			final Geolocation geolocation = trialInstanceGeolocationMap.get(trialInstance.toString());
			if (geolocation != null) {
				return Optional.of(geolocation);
			}
		}
		return Optional.absent();
	}

	@Override
	public void deleteExperimentDesign(final int studyId) {
		// Delete environment variables related to experiment design
		final Integer environmentDatasetId = this.getEnvironmentDatasetId(studyId);
		this.daoFactory.getProjectPropertyDAO()
			.deleteProjectVariables(environmentDatasetId, ExperimentDesignServiceImpl.EXPERIMENTAL_DESIGN_VARIABLES);

		// Delete variables related to experiment design and experiments of plot dataset
		final Integer plotDatasetId = this.getPlotDatasetId(studyId);
		this.daoFactory.getProjectPropertyDAO().deleteProjectVariablesByVariableTypes(plotDatasetId,
			Arrays.asList(VariableType.EXPERIMENTAL_DESIGN.getId(), TermId.MULTIFACTORIAL_INFO.getId()));
		this.daoFactory.getExperimentDao().deleteExperimentsForDataset(plotDatasetId);
	}

}
