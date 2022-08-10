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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

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

		// Automatically associate variables (TRAITS) to the study.
		this.associateVariablesToStudy(observations);

		final List<Integer> observationDbIds = new ArrayList<>();
		final Map<String, ExperimentModel> experimentModelMap = this.daoFactory.getExperimentDao()
			.getByObsUnitIds(observations.stream().map(ObservationDto::getObservationUnitDbId).collect(Collectors.toList())).stream()
			.collect(Collectors.toMap(ExperimentModel::getObsUnitId, Function.identity()));
		final Map<Integer, List<ValueReference>> validValuesForCategoricalVariables =
			this.getCategoriesForCategoricalVariables(observations);

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

	private void associateVariablesToStudy(final List<ObservationDto> observations) {

		// Get the observationVariableDbIds grouped by studyDbIds
		final Map<Integer, List<Integer>> studyDbIdsVariablesMap =
			observations.stream().collect(Collectors.groupingBy(o -> Integer.valueOf(o.getStudyDbId()),
				Collectors.mapping(o -> Integer.valueOf(o.getObservationVariableDbId()), toList())));

		// Get the existing TRAIT variables associated to the specified studyDbIds from the PLOT dataset.
		final Map<Integer, Map<Integer, ProjectProperty>> traitsByGeolocationIdsMap = this.daoFactory.getProjectPropertyDAO()
			.getProjectPropertiesMapByGeolocationIds(studyDbIdsVariablesMap.keySet(), Arrays.asList(DatasetTypeEnum.PLOT_DATA),
				Arrays.asList(
					VariableType.TRAIT));

		final List<Integer> variableIdsFromRequest = studyDbIdsVariablesMap.values().stream().flatMap(List::stream).collect(toList());
		final VariableFilter variableFilter = new VariableFilter();
		variableIdsFromRequest.forEach(variableFilter::addVariableId);
		final Map<Integer, Variable> variablesMap =
			this.daoFactory.getCvTermDao().getVariablesWithFilterById(variableFilter);

		final Map<Integer, List<VariableType>> variableTypesOfVariables =
			this.daoFactory.getCvTermPropertyDao().getByCvTermIdsAndType(variableIdsFromRequest, TermId.VARIABLE_TYPE.getId()).stream()
				.collect(groupingBy(CVTermProperty::getCvTermId, Collectors.mapping(o -> VariableType.getByName(o.getValue()), toList())));

		studyDbIdsVariablesMap.entrySet().forEach(entry -> {
			final Integer studyDbId = entry.getKey();
			final List<Integer> variableIds = entry.getValue();
			final Integer plotDataSetId =
				this.daoFactory.getDmsProjectDAO().getDatasetIdByEnvironmentIdAndDatasetType(studyDbId, DatasetTypeEnum.PLOT_DATA);
			variableIds.forEach(variableId -> {
				if (traitsByGeolocationIdsMap.containsKey(studyDbId) && !traitsByGeolocationIdsMap.get(studyDbId).containsKey(variableId)) {
					// Add a new projectprop record if the variable doesn't exist in the PLOT dataset.
					final Variable variable = variablesMap.get(variableId);
					if (variableTypesOfVariables.containsKey(variableId) && variableTypesOfVariables.get(variableId)
						.contains(VariableType.TRAIT)) {
						this.addProjectPropertyToDataset(plotDataSetId, variable, VariableType.TRAIT);
					}
				}
			});
		});
	}

	private void addProjectPropertyToDataset(final int projectId, final Variable variable, final VariableType variableType) {
		final ProjectPropertyDao projectPropertyDao = this.daoFactory.getProjectPropertyDAO();
		final ProjectProperty projectProperty = new ProjectProperty();
		projectProperty.setProject(new DmsProject(projectId));
		projectProperty.setTypeId(variableType.getId());
		projectProperty.setAlias(StringUtils.isEmpty(variable.getAlias()) ? variable.getAlias() : variable.getName());
		projectProperty.setVariableId(Integer.valueOf(variable.getId()));
		projectProperty.setRank(projectPropertyDao.getNextRank(projectId));
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
