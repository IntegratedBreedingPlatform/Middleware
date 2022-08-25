package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.observation.ObservationDto;
import org.generationcp.middleware.api.brapi.v2.observation.ObservationSearchRequestDto;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.PhenotypeExternalReference;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
@Transactional
public class ObservationServiceBrapiImpl implements ObservationServiceBrapi {

	private final DaoFactory daoFactory;
	private final HibernateSessionProvider sessionProvider;

	public ObservationServiceBrapiImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<ObservationDto> searchObservations(final ObservationSearchRequestDto observationSearchRequestDto, final Pageable pageable) {
		final List<ObservationDto> observationDtos =
			this.daoFactory.getPhenotypeDAO().searchObservations(observationSearchRequestDto, pageable);
		if (!CollectionUtils.isEmpty(observationDtos)) {
			final List<Integer> observationDbIds =
				new ArrayList<>(observationDtos.stream().map(o -> Integer.valueOf(o.getObservationDbId()))
					.collect(Collectors.toSet()));
			final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
				this.daoFactory.getPhenotypeExternalReferenceDAO().getExternalReferences(observationDbIds).stream()
					.collect(groupingBy(
						ExternalReferenceDTO::getEntityId));
			for (final ObservationDto observationDto : observationDtos) {
				observationDto.setExternalReferences(externalReferencesMap.get(observationDto.getObservationDbId()));
			}
		}
		return observationDtos;
	}

	@Override
	public long countObservations(final ObservationSearchRequestDto observationSearchRequestDto) {
		return this.daoFactory.getPhenotypeDAO().countObservations(observationSearchRequestDto);
	}

	@Override
	public List<ObservationDto> createObservations(final List<ObservationDto> observations) {

		final List<Integer> observationDbIds = new ArrayList<>();
		final Map<String, ExperimentModel> experimentModelMap = this.daoFactory.getExperimentDao()
			.getByObsUnitIds(observations.stream().map(ObservationDto::getObservationUnitDbId).collect(Collectors.toList())).stream()
			.collect(Collectors.toMap(ExperimentModel::getObsUnitId, Function.identity()));
		final Map<Integer, List<ValueReference>> validValuesForCategoricalVariables =
			this.getCategoriesForCategoricalVariables(observations);

		// Automatically associate variables to the study. This is to ensure that the TRAIT, ANALYSIS, ANALYSIS variables
		// are added to their respective dataset before creating the observation.
		this.associateVariablesToDatasets(observations, experimentModelMap);

		final PhenotypeDao dao = this.daoFactory.getPhenotypeDAO();
		for (final ObservationDto observation : observations) {
			final Phenotype phenotype = new Phenotype();
			final Date currentDate = new Date();
			phenotype.setCreatedDate(currentDate);
			phenotype.setUpdatedDate(currentDate);
			phenotype.setObservableId(Integer.valueOf(observation.getObservationVariableDbId()));
			phenotype.setValue(observation.getValue());
			final Optional<Integer> categoricalIdOptional =
				this.resolveCategoricalIdValue(Integer.valueOf(observation.getObservationVariableDbId()), observation.getValue(),
					validValuesForCategoricalVariables);
			if (categoricalIdOptional.isPresent()) {
				phenotype.setcValue(categoricalIdOptional.get());
			}
			phenotype.setExperiment(experimentModelMap.get(observation.getObservationUnitDbId()));
			this.setPhenotypeExternalReferences(observation, phenotype);
			final Phenotype saved = dao.save(phenotype);
			observationDbIds.add(saved.getPhenotypeId());
		}
		this.sessionProvider.getSession().flush();
		if (!CollectionUtils.isEmpty(observationDbIds)) {
			final ObservationSearchRequestDto searchRequestDTO = new ObservationSearchRequestDto();
			searchRequestDTO.setObservationDbIds(observationDbIds);
			return this.searchObservations(searchRequestDTO, null);
		} else {
			return new ArrayList<>();
		}
	}

	private void setPhenotypeExternalReferences(final ObservationDto observationDto, final Phenotype phenotype) {
		if (observationDto.getExternalReferences() != null) {
			final List<PhenotypeExternalReference> references = new ArrayList<>();
			observationDto.getExternalReferences().forEach(reference -> {
				final PhenotypeExternalReference externalReference =
					new PhenotypeExternalReference(phenotype, reference.getReferenceID(), reference.getReferenceSource());
				references.add(externalReference);
			});
			phenotype.setExternalReferences(references);
		}
	}

	private void associateVariablesToDatasets(final List<ObservationDto> observations,
		final Map<String, ExperimentModel> experimentModelMap) {

		// Get the variables grouped by dataset from the request observations
		final Map<Integer, Set<Integer>> projectVariablesMap =
			observations.stream()
				.collect(Collectors.groupingBy(o -> experimentModelMap.get(o.getObservationUnitDbId()).getProject().getProjectId(),
					Collectors.mapping(o -> Integer.valueOf(o.getObservationVariableDbId()), toSet())));

		final Map<Integer, DmsProject> dmsProjectMap = this.daoFactory.getDmsProjectDAO().getByIds(projectVariablesMap.keySet()).stream()
			.collect(toMap(DmsProject::getProjectId, Function.identity()));

		// Get the variables from the request observations
		final Set<Integer> variableIdsFromRequest = projectVariablesMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
		final VariableFilter variableFilter = new VariableFilter();
		variableIdsFromRequest.forEach(variableFilter::addVariableId);
		final Map<Integer, Variable> variablesMap =
			this.daoFactory.getCvTermDao().getVariablesWithFilterById(variableFilter);

		// Get the variableTypes of the variables from the request
		final Map<Integer, List<VariableType>> variableTypesOfVariables =
			this.daoFactory.getCvTermPropertyDao()
				.getByCvTermIdsAndType(new ArrayList<>(variableIdsFromRequest), TermId.VARIABLE_TYPE.getId()).stream()
				.collect(groupingBy(CVTermProperty::getCvTermId, Collectors.mapping(o -> VariableType.getByName(o.getValue()), toList())));

		// Get the existing variables associated to the datasets
		final Map<Integer, Map<Integer, ProjectProperty>> datasetVariablesMap =
			this.daoFactory.getProjectPropertyDAO().getPropsForProjectIds(new ArrayList<>(projectVariablesMap.keySet())).entrySet().stream()
				.collect(toMap(Map.Entry::getKey,
					entry -> entry.getValue().stream().collect(toMap(ProjectProperty::getVariableId, Function.identity()))));

		// Associate the variables to their respective datasets
		projectVariablesMap.entrySet().forEach(entry -> {
			final DmsProject dmsProject = dmsProjectMap.get(entry.getKey());
			final Set<Integer> variableIds = entry.getValue();
			variableIds.forEach(variableId -> {

				// Do not proceed if variable already exists in the target dataset
				if (datasetVariablesMap.containsKey(dmsProject.getProjectId()) && datasetVariablesMap.get(dmsProject.getProjectId())
					.containsKey(variableId))
					return;
				// Do not proceed if variable doesn't have variable types
				if (!variableTypesOfVariables.containsKey(variableId))
					return;

				this.assignVariableToDataset(variableTypesOfVariables, dmsProject, variableId, variablesMap.get(variableId));

			});
		});
	}

	private void assignVariableToDataset(final Map<Integer, List<VariableType>> variableTypesOfVariables, final DmsProject dmsProject,
		final Integer variableId, final Variable variable) {
		if ((dmsProject.getDatasetType().isObservationType() || dmsProject.getDatasetType().isSubObservationType())
			&& variableTypesOfVariables.get(variableId).contains(VariableType.TRAIT)) {
			this.addProjectPropertyToDataset(dmsProject, variable, VariableType.TRAIT);
		} else if ((dmsProject.getDatasetType().isObservationType() || dmsProject.getDatasetType().isSubObservationType())
			&& variableTypesOfVariables.get(variableId).contains(VariableType.SELECTION_METHOD)) {
			this.addProjectPropertyToDataset(dmsProject, variable, VariableType.SELECTION_METHOD);
		} else if (dmsProject.getDatasetType().getDatasetTypeId() == DatasetTypeEnum.MEANS_DATA.getId()
			&& variableTypesOfVariables.get(variableId).contains(VariableType.ANALYSIS)) {
			this.addProjectPropertyToDataset(dmsProject, variable, VariableType.ANALYSIS);
		} else if (dmsProject.getDatasetType().getDatasetTypeId() == DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId()
			&& variableTypesOfVariables.get(variableId).contains(VariableType.ANALYSIS_SUMMARY)) {
			this.addProjectPropertyToDataset(dmsProject, variable, VariableType.ANALYSIS_SUMMARY);
		}
	}

	private void addProjectPropertyToDataset(final DmsProject dmsProject, final Variable variable, final VariableType variableType) {
		final ProjectPropertyDao projectPropertyDao = this.daoFactory.getProjectPropertyDAO();
		final ProjectProperty projectProperty = new ProjectProperty();
		projectProperty.setProject(dmsProject);
		projectProperty.setTypeId(variableType.getId());
		projectProperty.setAlias(StringUtils.isEmpty(variable.getAlias()) ? variable.getName() : variable.getAlias());
		projectProperty.setVariableId(Integer.valueOf(variable.getId()));
		projectProperty.setRank(projectPropertyDao.getNextRank(dmsProject.getProjectId()));
		projectPropertyDao.save(projectProperty);
	}

	private Map<Integer, List<ValueReference>> getCategoriesForCategoricalVariables(final List<ObservationDto> observations) {
		final Set<Integer> variableIds =
			observations.stream().map(o -> Integer.valueOf(o.getObservationVariableDbId())).collect(Collectors.toSet());
		return this.daoFactory.getCvTermRelationshipDao().getCategoriesForCategoricalVariables(new ArrayList<>(variableIds));
	}

	private Optional<Integer> resolveCategoricalIdValue(final Integer variableId, final String value,
		final Map<Integer, List<ValueReference>> validValuesForCategoricalVariables) {
		if (validValuesForCategoricalVariables.containsKey(variableId)) {
			final Optional<ValueReference> match =
				validValuesForCategoricalVariables.get(variableId).stream().filter(v -> v.getName().equals(value)).findAny();
			return match.isPresent() ? Optional.of(Integer.valueOf(match.get().getKey())) : Optional.empty();
		}
		return Optional.empty();
	}
}
