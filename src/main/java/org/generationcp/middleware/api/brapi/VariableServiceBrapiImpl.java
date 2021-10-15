package org.generationcp.middleware.api.brapi;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.search_request.brapi.v2.VariableSearchRequestDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.CategoryDTO;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class VariableServiceBrapiImpl implements VariableServiceBrapi {

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private DatasetService datasetService;

	private DaoFactory daoFactory;

	public VariableServiceBrapiImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public VariableServiceBrapiImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<VariableDTO> getObservationVariables(final String crop, final VariableSearchRequestDTO requestDTO,
		final Pageable pageable) {
		final List<VariableDTO> variableDTOS = this.daoFactory.getCvTermDao().getObservationVariables(requestDTO, pageable);
		if (!CollectionUtils.isEmpty(variableDTOS)) {
			final List<Integer> variableIds = new ArrayList<>(variableDTOS.stream().map(v -> Integer.valueOf(v.getObservationVariableDbId()))
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
				dto.setCommonCropName(crop);
				dto.setCrop(crop);
				dto.setExternalReferences(externalReferencesMap.get(dto.getObservationVariableDbId()));
				dto.getScale().setExternalReferences(externalReferencesMap.get(dto.getScale().getScaleDbId()));
				dto.getMethod().setExternalReferences(externalReferencesMap.get(dto.getMethod().getMethodDbId()));
				dto.getTrait().setExternalReferences(externalReferencesMap.get(dto.getTrait().getTraitDbId()));
			}
		}
		return variableDTOS;
	}

	@Override
	public long countObservationVariables(final VariableSearchRequestDTO requestDTO) {
		return this.daoFactory.getCvTermDao().countObservationVariables(requestDTO);
	}

	@Override
	public VariableDTO updateObservationVariable(final VariableDTO variable) {
		final Integer variableId = Integer.valueOf(variable.getObservationVariableDbId());
		final boolean isVariableUsedInStudy = this.ontologyVariableDataManager.areVariablesUsedInStudy(Arrays.asList(variableId));
		this.updateVariable(variable, isVariableUsedInStudy);
		this.updateProperty(variable, isVariableUsedInStudy);
		this.updateMethod(variable, isVariableUsedInStudy);
		this.updateScale(variable, isVariableUsedInStudy);

		// Assign variable to studies
		if (!CollectionUtils.isEmpty(variable.getStudyDbIds())) {
			for (final String studyDbId : variable.getStudyDbIds()) {
				this.addObservationVariableToStudy(Integer.valueOf(studyDbId), Integer.valueOf(variable.getObservationVariableDbId()),
					variable.getObservationVariableName());
			}
		}
		return variable;
	}

	private void updateVariable(final VariableDTO variable, final boolean isVariableUsedInStudy) {
		final Integer variableId = Integer.valueOf(variable.getObservationVariableDbId());
		final CVTerm variableTerm = this.daoFactory.getCvTermDao().getById(variableId);
		if (!isVariableUsedInStudy) {
			// Updating of name, property, scale and method relationship is only applicable
			// if the variable is not used in study.
			variableTerm.setName(variable.getObservationVariableName());
			this.updatePropertyMethodScaleRelation(variable);
		}
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
			final CVTermRelationship cvTermRelationship = propertyRelation.get();
			cvTermRelationship.setObjectId(Integer.valueOf(variable.getTrait().getTraitDbId()));
			this.daoFactory.getCvTermRelationshipDao().update(cvTermRelationship);
		}
		if (methodRelation.isPresent()) {
			final CVTermRelationship cvTermRelationship = methodRelation.get();
			cvTermRelationship.setObjectId(Integer.valueOf(variable.getMethod().getMethodDbId()));
			this.daoFactory.getCvTermRelationshipDao().update(cvTermRelationship);
		}
		if (scaleRelation.isPresent()) {
			final CVTermRelationship cvTermRelationship = scaleRelation.get();

			final List<CVTermRelationship> dataTypeRelationShips = this.daoFactory.getCvTermRelationshipDao()
				.getBySubjectIdAndTypeId(cvTermRelationship.getObjectId(), TermId.HAS_TYPE.getId());

			// Update Datatype
			if (!CollectionUtils.isEmpty(dataTypeRelationShips) && StringUtils.isNotEmpty(variable.getScale().getDataType())) {
				final CVTermRelationship dataTypeRelationShip = dataTypeRelationShips.get(0);
				dataTypeRelationShip.setSubjectId(Integer.valueOf(variable.getScale().getScaleDbId()));
				this.daoFactory.getCvTermRelationshipDao().update(dataTypeRelationShip);
			}

			cvTermRelationship.setObjectId(Integer.valueOf(variable.getScale().getScaleDbId()));
			this.daoFactory.getCvTermRelationshipDao().update(cvTermRelationship);

		}

	}

	private void updateProperty(final VariableDTO variable, final boolean isVariableUsedInStudy) {
		final Integer propertyId = Integer.valueOf(variable.getTrait().getTraitDbId());
		final CVTerm propertyTerm = this.daoFactory.getCvTermDao().getById(propertyId);
		if (!isVariableUsedInStudy) {
			// Updating of property name is only applicable
			// if the variable is not used in study.
			propertyTerm.setName(variable.getTrait().getTraitName());
		}
		propertyTerm.setDefinition(variable.getTrait().getTraitDescription());
		this.daoFactory.getCvTermDao().update(propertyTerm);
	}

	private void updateMethod(final VariableDTO variable, final boolean isVariableUsedInStudy) {
		final Integer methodId = Integer.valueOf(variable.getMethod().getMethodDbId());
		final CVTerm methodTerm = this.daoFactory.getCvTermDao().getById(methodId);
		if (!isVariableUsedInStudy) {
			// Updating of method name is only applicable
			// if the variable is not used in study.
			methodTerm.setName(variable.getMethod().getMethodName());
		}
		methodTerm.setDefinition(variable.getMethod().getDescription());
		this.daoFactory.getCvTermDao().update(methodTerm);
	}

	private void updateScale(final VariableDTO variable, final boolean isVariableUsedInStudy) {
		final Integer scaleId = Integer.valueOf(variable.getScale().getScaleDbId());
		final CVTerm scaleTerm = this.daoFactory.getCvTermDao().getById(scaleId);

		// Only update the categorical values and min, max if variable is not used in study
		if (!isVariableUsedInStudy) {

			// Updating of scale name, categories, min, max is only applicable
			// if the variable is not used in study.
			scaleTerm.setName(variable.getScale().getScaleName());

			final List<CVTermRelationship> relationships = this.daoFactory.getCvTermRelationshipDao().getBySubject(scaleTerm.getCvTermId());
			final Optional<CVTermRelationship>
				dataTypeRelationship = relationships.stream().filter(r -> r.getTypeId() == TermId.HAS_TYPE.getId()).findAny();
			final DataType dataType = dataTypeRelationship.isPresent() ? DataType.getById(dataTypeRelationship.get().getObjectId()) : null;

			// Update valid values if datatype is categorical, this only updates the label
			if (dataType == DataType.CATEGORICAL_VARIABLE) {
				final List<Integer> valueIds =
					relationships.stream().filter(r -> r.getTypeId() == TermId.HAS_VALUE.getId()).map(CVTermRelationship::getObjectId)
						.collect(Collectors.toList());
				final Map<String, CVTerm> categoricalValuesMap = this.daoFactory.getCvTermDao().getByIds(valueIds).stream()
					.collect(Collectors.toMap(CVTerm::getName, Function.identity()));

				for (final CategoryDTO categoryDTO : variable.getScale().getValidValues().getCategories()) {
					if (categoricalValuesMap.containsKey(categoryDTO.getValue())) {
						final CVTerm categoricalValueTerm = categoricalValuesMap.get(categoryDTO.getValue());
						categoricalValueTerm.setDefinition(categoryDTO.getLabel());
						this.daoFactory.getCvTermDao().update(categoricalValueTerm);
					}
				}
			}

			// Update min and max if datatype is numerical
			if (dataType == DataType.NUMERIC_VARIABLE) {
				final int maxTermId = TermId.MAX_VALUE.getId();
				final int minTermId = TermId.MIN_VALUE.getId();
				final Integer minScale = variable.getScale().getValidValues().getMin();
				final Integer maxScale = variable.getScale().getValidValues().getMax();
				this.updateScaleMinMax(scaleId, minScale, minTermId);
				this.updateScaleMinMax(scaleId, maxScale, maxTermId);
			}

			this.daoFactory.getCvTermDao().update(scaleTerm);

		}

	}

	private void updateScaleMinMax(final Integer scaleId, final Integer scaleSize, final int termId) {
		if (scaleSize != null) {
			this.daoFactory.getCvTermPropertyDao().save(scaleId, termId, String.valueOf(scaleSize), 0);
		} else {
			final CVTermProperty property = this.daoFactory.getCvTermPropertyDao().getOneByCvTermAndType(scaleId, termId);
			if (property != null) {
				this.daoFactory.getCvTermPropertyDao().makeTransient(property);
			}
		}
	}

	private void addObservationVariableToStudy(final Integer studyDbId, final Integer observationVariableDbId,
		final String observationVariableName) {
		final Integer projectId =
			this.daoFactory.getDmsProjectDAO().getDatasetIdByEnvironmentIdAndDatasetType(studyDbId, DatasetTypeEnum.PLOT_DATA);
		final List<MeasurementVariable> studyMeasurementVariables =
			this.datasetService.getObservationSetVariables(projectId, Arrays.asList(VariableType.TRAIT.getId()));
		// Check first if the variable already exists in study
		if (studyMeasurementVariables.stream().noneMatch(o -> o.getTermId() == observationVariableDbId)) {
			this.datasetService.addDatasetVariable(projectId, observationVariableDbId, VariableType.TRAIT, observationVariableName);
		}
	}
}
