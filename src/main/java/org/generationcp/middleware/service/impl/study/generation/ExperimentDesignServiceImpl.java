package org.generationcp.middleware.service.impl.study.generation;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Transactional
public class ExperimentDesignServiceImpl implements ExperimentDesignService {

	@Autowired
	private StudyService studyService;

	private static final List<Integer> FIELDMAP_ENVT_VARIABLES = Collections.singletonList(TermId.BLOCK_ID.getId());

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
		this.experimentGenerator = new ExperimentModelGenerator();
	}

	@Override
	public void saveExperimentDesign(final CropType crop, final int studyId, final List<MeasurementVariable> variables,
		final Map<Integer, List<ObservationUnitRow>> instanceRowsMap) {

		Preconditions.checkNotNull(crop);
		Preconditions.checkState(!CollectionUtils.isEmpty(variables));
		Preconditions.checkState(!CollectionUtils.isEmpty(instanceRowsMap));
		for (final Integer instance : instanceRowsMap.keySet()) {
			Preconditions.checkState(!CollectionUtils.isEmpty(instanceRowsMap.get(instance)));
		}

		// Delete previous experiments from the specified instances (if any)
		final List<Integer> instanceNumbers = Lists.newArrayList(instanceRowsMap.keySet());
		final Integer plotDatasetId = this.studyService.getPlotDatasetId(studyId);
		final Integer environmentDatasetId = this.studyService.getEnvironmentDatasetId(studyId);
		this.deleteTrialInstanceExperiments(plotDatasetId, environmentDatasetId, instanceNumbers);

		// Save variables at trial and plot dataset level
		final List<ExperimentModel> environments = this.daoFactory.getInstanceDao()
			.getEnvironmentsForInstances(studyId, instanceNumbers);
		this.saveVariables(variables, plotDatasetId, environmentDatasetId, environments);

		// Save experiments and stocks (if applicable) in plot dataset
		this.saveObservationUnitRows(crop, plotDatasetId, variables, instanceRowsMap, environments);

	}

	@Override
	public Optional<Integer> getStudyExperimentDesignTypeTermId(final int studyId) {
		final Integer environmentDatasetId = this.studyService.getEnvironmentDatasetId(studyId);
		final ProjectProperty projectProp = this.daoFactory.getProjectPropertyDAO()
			.getByStandardVariableId(new DmsProject(environmentDatasetId), TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		if (projectProp != null && projectProp.getValue() != null && NumberUtils.isDigits(projectProp.getValue())) {
			return Optional.of(Integer.valueOf(projectProp.getValue()));
		}
		return Optional.empty();
	}

	private void saveVariables(final List<MeasurementVariable> variables, final Integer plotDatasetId, final Integer environmentDatasetId,  final List<ExperimentModel> environments) {
		int plotDatasetNextRank = this.daoFactory.getProjectPropertyDAO().getNextRank(plotDatasetId);
		final List<Integer> plotVariableIds = this.daoFactory.getProjectPropertyDAO().getVariableIdsForDataset(plotDatasetId);

		int envDatasetNextRank = this.daoFactory.getProjectPropertyDAO().getNextRank(environmentDatasetId);
		final List<Integer> envVariableIds = this.daoFactory.getProjectPropertyDAO().getVariableIdsForDataset(environmentDatasetId);
		// Save project variables in environment and plot datasets
		for (final MeasurementVariable variable : variables) {
			final int variableId = variable.getTermId();
			final VariableType variableType = variable.getVariableType();
			final boolean isEnvironmentVariable = VariableType.ENVIRONMENT_DETAIL.equals(variableType);
			int rank = 1;
			final String value = variable.getValue();
			if (!this.variableExists(variableId, isEnvironmentVariable, envVariableIds, plotVariableIds)) {
				Integer projectId = plotDatasetId;
				Integer variableTypeId = variableType.getId();
				if (isEnvironmentVariable) {
					projectId = environmentDatasetId;
					rank = envDatasetNextRank++;

				} else {
					// FIXME: Current bug that saves treatment factor as projectprop with type_id = 1100. Should be 1809
					if (VariableType.TREATMENT_FACTOR.equals(variableType)) {
						variableTypeId = TermId.MULTIFACTORIAL_INFO.getId();

						// Save a record for same variable with variable type = EXPERIMENT_DESIGN
						final ProjectProperty property =
							new ProjectProperty(new DmsProject(projectId), VariableType.EXPERIMENTAL_DESIGN.getId(), "", plotDatasetNextRank++, variableId, variable.getAlias());
						this.daoFactory.getProjectPropertyDAO().save(property);
					}
					rank = plotDatasetNextRank++;
				}
				final ProjectProperty property =
					new ProjectProperty(new DmsProject(projectId), variableTypeId, value, rank, variableId, variable.getAlias());
				this.daoFactory.getProjectPropertyDAO().save(property);
			}

			// FIXME Undo this duplicate saving in nd_experimentprop (logged as part of IBP-3150)
			if (isEnvironmentVariable && EXPERIMENTAL_DESIGN_VARIABLES.contains(variableId)) {
				for (final ExperimentModel environment : environments) {
					final ExperimentProperty environmentProperty = new ExperimentProperty(environment, value, rank, variableId);
					this.daoFactory.getEnvironmentPropertyDao().save(environmentProperty);
				}
			}

		}
	}

	private boolean variableExists(final Integer variableId,final Boolean isEnvironmentVariable, final List<Integer> environmentVariableIds, final List<Integer> plotVariableIds) {
		return isEnvironmentVariable ? environmentVariableIds.contains(variableId) : plotVariableIds.contains(variableId);
	}

	private void saveObservationUnitRows(final CropType crop, final Integer plotDatasetId,
		final List<MeasurementVariable> variables, 	final Map<Integer, List<ObservationUnitRow>> instanceRowsMap, final List<ExperimentModel> environments) {

		// Retrieved any previously saved stocks. We are not using Immutable map for the case of no stocks yet
		final Set<StockModel> stocks = this.daoFactory.getStockDao().findInDataSet(plotDatasetId);
		final ImmutableMap<Integer, ExperimentModel> trialInstanceEnvironmentMap =
			Maps.uniqueIndex(environments, new Function<ExperimentModel, Integer>() {
				@Override
				public Integer apply(final ExperimentModel environment) {
					return environment.getObservationUnitNo();
				}
			});

		final Map<String, StockModel> stocksMap = new HashMap<>();
		for (final StockModel stock : stocks) {
			stocksMap.put(stock.getUniqueName(), stock);
		}

		final ImmutableMap<Integer, MeasurementVariable> variablesMap =
			Maps.uniqueIndex(variables, new Function<MeasurementVariable, Integer>() {

				@Override
				public Integer apply(final MeasurementVariable measurementVariable) {
					return measurementVariable.getTermId();
				}
			});

		for (final Map.Entry<Integer, List<ObservationUnitRow>> instanceRows : instanceRowsMap.entrySet()) {
			final Integer trialInstance = instanceRows.getKey();
			for (final ObservationUnitRow row:  instanceRowsMap.get(trialInstance)) {
				final ExperimentModel
					experimentModel = this.experimentGenerator.generate(crop, plotDatasetId, row, ExperimentType.PLOT, variablesMap);
				final String entryNumber = String.valueOf(row.getEntryNumber());
				StockModel stockModel = stocksMap.get(entryNumber);
				if (stockModel == null) {
					stockModel = new StockModelGenerator().generate(variablesMap, Lists.newArrayList(row.getVariables().values()));
					this.daoFactory.getStockDao().save(stockModel);
					stocksMap.put(entryNumber, stockModel);
				}
				experimentModel.setStock(stockModel);
				final ExperimentModel environment = trialInstanceEnvironmentMap.get(trialInstance);
				if (environment != null) {
					experimentModel.setParent(environment);
				}
				this.daoFactory.getExperimentDao().save(experimentModel);
			}
		}
	}

	private void deleteTrialInstanceExperiments(final Integer plotDatasetId, final Integer environmentDatasetId, final List<Integer> instanceNumbers) {
		this.daoFactory.getExperimentDao().deleteExperimentsForDatasetInstances(plotDatasetId, instanceNumbers);
		final List<Integer> environmentVariables = Lists.newArrayList(Iterables.concat(EXPERIMENTAL_DESIGN_VARIABLES, FIELDMAP_ENVT_VARIABLES));
		this.daoFactory.getEnvironmentPropertyDao().deletePropertiesInDatasetInstances(environmentDatasetId, instanceNumbers, environmentVariables);
	}


	@Override
	public void deleteStudyExperimentDesign(final int studyId) {
		// Delete environment variables related to experiment design and fieldmap
		final List<Integer> environmentVariables = Lists.newArrayList(Iterables.concat(EXPERIMENTAL_DESIGN_VARIABLES, FIELDMAP_ENVT_VARIABLES));
		final Integer environmentDatasetId = this.studyService.getEnvironmentDatasetId(studyId);
		this.daoFactory.getProjectPropertyDAO()
			.deleteProjectVariables(environmentDatasetId, environmentVariables);
		this.daoFactory.getEnvironmentPropertyDao().deletePropertiesInDataset(environmentDatasetId, environmentVariables);

		// Delete variables related to experiment design and experiments of plot dataset
		final Integer plotDatasetId = this.studyService.getPlotDatasetId(studyId);
		this.daoFactory.getProjectPropertyDAO().deleteDatasetVariablesByVariableTypes(plotDatasetId,
			Arrays.asList(VariableType.EXPERIMENTAL_DESIGN.getId(), TermId.MULTIFACTORIAL_INFO.getId()));
		this.daoFactory.getExperimentDao().deleteExperimentsForDataset(plotDatasetId);
	}

	void setStudyService(final StudyService studyService) {
		this.studyService = studyService;
	}
}
