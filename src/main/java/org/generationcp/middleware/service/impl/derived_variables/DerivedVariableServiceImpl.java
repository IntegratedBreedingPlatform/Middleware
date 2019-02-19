package org.generationcp.middleware.service.impl.derived_variables;

import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.domain.dataset.ObservationDto;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.FormulaVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.derived_variables.DerivedVariableService;
import org.generationcp.middleware.service.api.derived_variables.FormulaService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DerivedVariableServiceImpl implements DerivedVariableService {

	@Autowired
	private FormulaService formulaService;

	@Autowired
	private DatasetService datasetService;

	private DaoFactory daoFactory;

	public DerivedVariableServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public DerivedVariableServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	/**
	 * Gets the list of formula dependencies of all derived variables that are not yet loaded in a dataset.
	 *
	 * @param studyId
	 * @param datasetId
	 * @return
	 */
	@Override
	public Set<String> getDependencyVariables(final int datasetId) {

		final Set<Integer> variableIdsOfTraitsInStudy = this.getVariableIdsOfTraitsInDataset(datasetId);
		final Set<String> derivedVariablesDependencies = new HashSet<>();

		final Set<FormulaVariable> formulaVariables = this.formulaService.getAllFormulaVariables(variableIdsOfTraitsInStudy);
		for (final FormulaVariable formulaVariable : formulaVariables) {
			if (!variableIdsOfTraitsInStudy.contains(formulaVariable.getId())) {
				derivedVariablesDependencies.add(formulaVariable.getName());
			}
		}

		return derivedVariablesDependencies;
	}

	/**
	 * Gets the list of formula dependencies of specific derived variables that are not yet loaded in a dataset.
	 *
	 * @param studyId
	 * @param datasetId
	 * @param variableId
	 * @return
	 */
	@Override
	public Set<String> getDependencyVariables(final int datasetId, final int variableId) {

		final List<Integer> variableIds = Arrays.asList(variableId);
		final Set<Integer> variableIdsOfTraitsInStudy = this.getVariableIdsOfTraitsInDataset(datasetId);
		final Set<String> derivedVariablesDependencies = new HashSet<>();
		final Set<FormulaVariable> formulaVariables = this.formulaService.getAllFormulaVariables(new HashSet<Integer>(variableIds));
		for (final FormulaVariable formulaVariable : formulaVariables) {
			if (!variableIdsOfTraitsInStudy.contains(formulaVariable.getId())) {
				derivedVariablesDependencies.add(formulaVariable.getName());
			}
		}

		return derivedVariablesDependencies;
	}

	@Override
	public int countCalculatedVariablesInDatasets(final Set<Integer> datasetIds) {
		return this.daoFactory.getDmsProjectDAO().countCalculatedVariablesInDatasets(datasetIds);
	}

	protected Set<Integer> getVariableIdsOfTraitsInDataset(final int datasetId) {

		final Set<Integer> variableIdsOfTraitsInDataset = new HashSet<>();
		final List<MeasurementVariable> traits =
			datasetService.getMeasurementVariables(datasetId, Arrays.asList(VariableType.TRAIT.getId()));

		if (!traits.isEmpty()) {
			for (final MeasurementVariable trait : traits) {
				variableIdsOfTraitsInDataset.add(trait.getTermId());
			}
		}

		return variableIdsOfTraitsInDataset;

	}

	@Override
	public void saveCalculatedResult(
		final String value, final Integer categoricalId, final Integer observationUnitId, final Integer observationId,
		final MeasurementVariable measurementVariable) {

		// Update phenotype if it already exists, otherwise, create new phenotype.
		if (observationId != null) {
			this.updatePhenotype(observationId, categoricalId, value);
		} else {
			final ObservationDto observationDto = new ObservationDto();
			observationDto.setVariableId(measurementVariable.getTermId());
			observationDto.setCategoricalValueId(categoricalId);
			observationDto.setObservationUnitId(observationUnitId);
			observationDto.setValue(value);
			this.createPhenotype(observationDto);
		}

	}

	@Override
	public Map<Integer, MeasurementVariable> createVariableIdMeasurementVariableMap(final int datasetId) {
		final Map<Integer, MeasurementVariable> variableIdMeasurementVariableMap = new HashMap<>();
		final List<MeasurementVariable> measurementVariables =
			this.datasetService.getMeasurementVariables(datasetId, Arrays.asList(VariableType.TRAIT.getId()));
		for (final MeasurementVariable measurementVariable : measurementVariables) {
			variableIdMeasurementVariableMap.put(measurementVariable.getTermId(), measurementVariable);
		}
		return variableIdMeasurementVariableMap;
	}

	protected Phenotype updatePhenotype(final Integer observationId, final Integer categoricalValueId, final String value) {
		final PhenotypeDao phenotypeDao = this.daoFactory.getPhenotypeDAO();
		final Phenotype phenotype = phenotypeDao.getById(observationId);
		phenotype.setValue(value);
		phenotype.setcValue(categoricalValueId == null || categoricalValueId == 0 ? null : categoricalValueId);
		phenotype.setChanged(true);
		phenotype.setValueStatus(null);
		phenotypeDao.update(phenotype);
		return phenotype;
	}

	protected void createPhenotype(final ObservationDto observation) {
		final Phenotype phenotype = new Phenotype();
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());
		phenotype.setcValue(observation.getCategoricalValueId());
		final Integer variableId = observation.getVariableId();
		phenotype.setObservableId(variableId);
		phenotype.setValue(observation.getValue());
		final Integer observationUnitId = observation.getObservationUnitId();
		phenotype.setExperiment(new ExperimentModel(observationUnitId));
		phenotype.setName(String.valueOf(variableId));
		this.daoFactory.getPhenotypeDAO().save(phenotype);
	}

}
