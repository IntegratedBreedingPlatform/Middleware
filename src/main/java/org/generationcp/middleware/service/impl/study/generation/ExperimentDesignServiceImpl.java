package org.generationcp.middleware.service.impl.study.generation;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
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
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ExperimentDesignServiceImpl implements ExperimentDesignService {

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

	private void saveVariables(final int studyId, final List<MeasurementVariable> variables, final Integer plotDatasetId) {
		final int plotDatasetNextRank = this.daoFactory.getProjectPropertyDAO().getNextRank(plotDatasetId);
		final List<Integer> plotVariableIds = this.daoFactory.getProjectPropertyDAO().getVariableIdsForDataset(plotDatasetId);

		final Integer envDatasetId = this.getEnvironmentDatasetId(studyId);
		final int envDatasetNextRank = this.daoFactory.getProjectPropertyDAO().getNextRank(envDatasetId);
		final List<Integer> envVariableIds = this.daoFactory.getProjectPropertyDAO().getVariableIdsForDataset(envDatasetId);
		// Save project variables in environment and plot datasets
		for (final MeasurementVariable variable : variables) {
			final int variableId = variable.getTermId();
			final VariableType variableType = variable.getVariableType();
			final boolean isEnvironmentVariable = VariableType.ENVIRONMENT_DETAIL.equals(variableType);
			if (!this.variableExists(variableId, isEnvironmentVariable, envVariableIds, plotVariableIds)) {
				Integer projectId = plotDatasetId;
				Integer rank = plotDatasetNextRank;
				if (isEnvironmentVariable) {
					projectId = envDatasetId;
					rank = envDatasetNextRank;
				}
				final DmsProject project = new DmsProject();
				project.setProjectId(projectId);
				final ProjectProperty property =
					new ProjectProperty(project, variableType.getId(), variable.getValue(), rank, variableId, variable.getAlias());
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
				experimentModel = this.experimentGenerator.generate(crop, plotDatasetId, row, ExperimentType.PLOT, geolocation, variables);
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
		// FIXME: Delete variables of environment dataset related to EXP DESIGN
		final Integer environmentDatasetId = this.getEnvironmentDatasetId(studyId);
		this.daoFactory.getProjectPropertyDAO().deleteProjectVariablesByVariableTypes(environmentDatasetId,
			Collections.singletonList(VariableType.EXPERIMENTAL_DESIGN.getId()));

		// Delete experiments of plot dataset
		final Integer plotDatasetId = this.getPlotDatasetId(studyId);
		this.daoFactory.getExperimentDao().deleteExperimentsForDataset(plotDatasetId);
	}

}
