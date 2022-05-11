package org.generationcp.middleware.api.brapi;

import com.google.common.collect.Multimap;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.search_request.brapi.v2.VariableSearchRequestDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.service.api.analysis.SiteAnalysisService;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.VariableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class VariableServiceBrapiImpl implements VariableServiceBrapi {

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private OntologyVariableService ontologyVariableService;

	@Autowired
	private DatasetService datasetService;

	@Autowired
	private SiteAnalysisService siteAnalysisService;

	private DaoFactory daoFactory;

	public VariableServiceBrapiImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public VariableServiceBrapiImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<VariableDTO> getVariables(final VariableSearchRequestDTO requestDTO, final Pageable pageable,
		final VariableTypeGroup variableTypeGroup) {
		final List<VariableDTO> variableDTOS = this.daoFactory.getCvTermDao().getVariableDTOS(requestDTO, pageable, variableTypeGroup);
		if (!CollectionUtils.isEmpty(variableDTOS)) {
			final List<Integer> variableIds =
				new ArrayList<>(variableDTOS.stream().map(v -> Integer.valueOf(v.getObservationVariableDbId()))
					.collect(Collectors.toSet()));
			variableIds.addAll(variableDTOS.stream().map(variableDTO -> Integer.valueOf(variableDTO.getTrait().getTraitDbId()))
				.collect(Collectors.toSet()));
			variableIds.addAll(variableDTOS.stream().map(variableDTO -> Integer.valueOf(variableDTO.getScale().getScaleDbId()))
				.collect(Collectors.toSet()));
			variableIds.addAll(variableDTOS.stream().map(variableDTO -> Integer.valueOf(variableDTO.getMethod().getMethodDbId()))
				.collect(Collectors.toSet()));

			final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
				this.daoFactory.getCvTermExternalReferenceDAO().getExternalReferences(variableIds).stream()
					.collect(groupingBy(
						ExternalReferenceDTO::getEntityId));
			for (final VariableDTO dto : variableDTOS) {
				dto.setExternalReferences(externalReferencesMap.get(dto.getObservationVariableDbId()));
				dto.getScale().setExternalReferences(externalReferencesMap.get(dto.getScale().getScaleDbId()));
				dto.getMethod().setExternalReferences(externalReferencesMap.get(dto.getMethod().getMethodDbId()));
				dto.getTrait().setExternalReferences(externalReferencesMap.get(dto.getTrait().getTraitDbId()));
			}

			final Multimap<Integer, VariableType> variableTypeMultimap =
				this.ontologyVariableService.getVariableTypesOfVariables(variableIds);
			for (final VariableDTO dto : variableDTOS) {
				if (variableTypeMultimap.get(Integer.valueOf(dto.getObservationVariableDbId())).contains(VariableType.ANALYSIS)) {
					dto.getContextOfUse().add(VariableDTO.ContextOfUseEnum.MEANS.toString());
				} else {
					dto.getContextOfUse().add(VariableDTO.ContextOfUseEnum.PLOT.toString());
				}
			}

		}
		return variableDTOS;
	}

	@Override
	public long countVariables(final VariableSearchRequestDTO requestDTO, final VariableTypeGroup variableTypeGroup) {
		return this.daoFactory.getCvTermDao().countVariables(requestDTO, variableTypeGroup);
	}

	@Override
	public VariableDTO updateObservationVariable(final VariableDTO variable) {
		final Integer variableId = Integer.valueOf(variable.getObservationVariableDbId());
		final boolean isVariableUsedInStudy = this.ontologyVariableDataManager
			.areVariablesUsedInStudy(Arrays.asList(variableId));
		if (!isVariableUsedInStudy) {
			this.updateVariable(variable);
		}

		// Assign variable to studies
		if (!CollectionUtils.isEmpty(variable.getStudyDbIds())) {
			final List<Integer> plotDatasetIds = new ArrayList<>();
			final List<Integer> meansDatasetIds = new ArrayList<>();
			for (final String studyDbId : variable.getStudyDbIds()) {
				this.addObservationVariableToStudy(Integer.valueOf(studyDbId),
					Integer.valueOf(variable.getObservationVariableDbId()), variable.getObservationVariableName(), plotDatasetIds,
					meansDatasetIds, variable.getContextOfUse());
			}
		}
		return variable;
	}

	@Override
	public List<VariableDTO> createObservationVariables(final List<VariableDTO> variableDTOList) {
		for (final VariableDTO variableDTO : variableDTOList) {
			final OntologyVariableInfo variableInfo = new OntologyVariableInfo();
			variableInfo.setName(variableDTO.getObservationVariableName());
			variableInfo.setMethodId(Integer.valueOf(variableDTO.getMethod().getMethodDbId()));
			variableInfo.setPropertyId(Integer.valueOf(variableDTO.getTrait().getTraitDbId()));
			variableInfo.setScaleId(Integer.valueOf(variableDTO.getScale().getScaleDbId()));

			if (variableDTO.getScale().getValidValues() != null && variableDTO.getScale().getValidValues().getMin() != null) {
				variableInfo.setExpectedMin(String.valueOf(variableDTO.getScale().getValidValues().getMin()));
			}

			if (variableDTO.getScale().getValidValues() != null && variableDTO.getScale().getValidValues().getMax() != null) {
				variableInfo.setExpectedMax(String.valueOf(variableDTO.getScale().getValidValues().getMax()));
			}

			// If ContextOfUse is MEANS, it means the variable should have a varibleType = ANALYSIS
			if (variableDTO.getContextOfUse() != null && variableDTO.getContextOfUse()
				.contains(VariableDTO.ContextOfUseEnum.MEANS.toString())) {
				variableInfo.addVariableType(VariableType.ANALYSIS);
			} else {
				// Default variableType is TRAIT
				variableInfo.addVariableType(VariableType.TRAIT);
			}

			this.ontologyVariableDataManager.addVariable(variableInfo);
			variableDTO.setObservationVariableDbId(String.valueOf(variableInfo.getId()));
		}
		return variableDTOList;
	}

	private void updateVariable(final VariableDTO variable) {
		final Integer variableId = Integer.valueOf(variable.getObservationVariableDbId());
		final CVTerm variableTerm = this.daoFactory.getCvTermDao().getById(variableId);

		// Updating of name, property, scale and method relationship is only applicable
		variableTerm.setName(variable.getObservationVariableName());
		this.updatePropertyMethodScaleRelation(variable);
		this.daoFactory.getCvTermDao().update(variableTerm);
	}

	private void updatePropertyMethodScaleRelation(final VariableDTO variable) {
		// load scale, method and property relationships
		final int variableId = Integer.parseInt(variable.getObservationVariableDbId());
		final List<CVTermRelationship> relationships = this.daoFactory.getCvTermRelationshipDao().getBySubject(variableId);
		final Optional<CVTermRelationship> methodRelation =
			relationships.stream().filter(r -> Objects.equals(r.getTypeId(), TermRelationshipId.HAS_METHOD.getId())).findAny();
		final Optional<CVTermRelationship> propertyRelation =
			relationships.stream().filter(r -> Objects.equals(r.getTypeId(), TermRelationshipId.HAS_PROPERTY.getId())).findAny();
		final Optional<CVTermRelationship> scaleRelation =
			relationships.stream().filter(r -> Objects.equals(r.getTypeId(), TermRelationshipId.HAS_SCALE.getId())).findAny();

		if (propertyRelation.isPresent()) {
			this.updateVariableRelationship(propertyRelation.get(), variable.getTrait().getTraitDbId());
		}
		if (methodRelation.isPresent()) {
			this.updateVariableRelationship(methodRelation.get(), variable.getMethod().getMethodDbId());
		}
		if (scaleRelation.isPresent()) {
			this.updateVariableRelationship(scaleRelation.get(), variable.getScale().getScaleDbId());
		}

	}

	private void updateVariableRelationship(final CVTermRelationship cvTermRelationship, final String objectId) {
		cvTermRelationship.setObjectId(Integer.valueOf(objectId));
		this.daoFactory.getCvTermRelationshipDao().update(cvTermRelationship);
	}

	private void addObservationVariableToStudy(
		final Integer studyDbId, final Integer observationVariableDbId,
		final String observationVariableName, final List<Integer> plotDatasetIds, final List<Integer> meansDatasetIds,
		final List<String> contextOfUse) {
		if (!CollectionUtils.isEmpty(contextOfUse) && contextOfUse.stream()
			.anyMatch(o -> VariableDTO.ContextOfUseEnum.MEANS.toString().equalsIgnoreCase(o))) {
			Integer meansDatasetId = this.daoFactory
				.getDmsProjectDAO().getDatasetIdByEnvironmentIdAndDatasetType(studyDbId, DatasetTypeEnum.MEANS_DATA);
			// If means dataset is not yet existing create new.
			if (meansDatasetId == null) {
				final int studyId = this.daoFactory.getDmsProjectDAO().getProjectIdByStudyDbId(studyDbId);
				meansDatasetId = this.siteAnalysisService.createMeansDataset(studyId);
			}
			if (!meansDatasetIds.contains(meansDatasetId)) {
				final List<MeasurementVariable> studyMeasurementVariables =
					this.datasetService.getObservationSetVariables(meansDatasetId, Arrays.asList(VariableType.ANALYSIS.getId()));
				// Check first if the variable already exists in study
				if (studyMeasurementVariables.stream().noneMatch(o -> o.getTermId() == observationVariableDbId)) {
					this.datasetService
						.addDatasetVariable(meansDatasetId, observationVariableDbId, VariableType.ANALYSIS, observationVariableName);
				}
				meansDatasetIds.add(meansDatasetId);
			}
		} else {
			final Integer plotDatasetId = this.daoFactory
				.getDmsProjectDAO().getDatasetIdByEnvironmentIdAndDatasetType(studyDbId, DatasetTypeEnum.PLOT_DATA);
			if (!plotDatasetIds.contains(plotDatasetId)) {
				final List<MeasurementVariable> studyMeasurementVariables =
					this.datasetService.getObservationSetVariables(plotDatasetId, Arrays.asList(VariableType.TRAIT.getId()));
				// Check first if the variable already exists in study
				if (studyMeasurementVariables.stream().noneMatch(o -> o.getTermId() == observationVariableDbId)) {
					this.datasetService
						.addDatasetVariable(plotDatasetId, observationVariableDbId, VariableType.TRAIT, observationVariableName);
				}
				plotDatasetIds.add(plotDatasetId);
			}
		}

	}

}
