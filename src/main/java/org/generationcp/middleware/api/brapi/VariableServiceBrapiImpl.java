package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.search_request.brapi.v2.VariableSearchRequestDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
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
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class VariableServiceBrapiImpl implements VariableServiceBrapi {

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private OntologyPropertyDataManager ontologyPropertyDataManager;

	@Autowired
	private OntologyMethodDataManager ontologyMethodDataManager;

	@Autowired
	private OntologyScaleDataManager ontologyScaleDataManager;

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
			final List<String> variableIds = new ArrayList<>(variableDTOS.stream().map(VariableDTO::getObservationVariableDbId)
				.collect(Collectors.toSet()));
			variableIds.addAll(variableDTOS.stream().map(variableDTO -> variableDTO.getTrait().getTraitDbId())
				.collect(Collectors.toSet()));
			variableIds.addAll(variableDTOS.stream().map(variableDTO -> variableDTO.getScale().getScaleDbId())
				.collect(Collectors.toSet()));
			variableIds.addAll(variableDTOS.stream().map(variableDTO -> variableDTO.getMethod().getMethodDbId())
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
	public void updateObservationVariable(final VariableDTO variable) {

		final OntologyVariableInfo ontologyVariableInfo = new OntologyVariableInfo();
		ontologyVariableInfo.setId(Integer.valueOf(variable.getObservationVariableDbId()));
		ontologyVariableInfo.setName(variable.getObservationVariableName());
		ontologyVariableInfo.setPropertyId(Integer.valueOf(variable.getTrait().getTraitDbId()));
		ontologyVariableInfo.setMethodId(Integer.valueOf(variable.getMethod().getMethodDbId()));
		ontologyVariableInfo.setScaleId(Integer.valueOf(variable.getScale().getScaleDbId()));
		ontologyVariableInfo.setIsFavorite(Boolean.FALSE);
		this.ontologyVariableDataManager.updateVariable(ontologyVariableInfo);

		final Property property = new Property();
		property.setId(Integer.valueOf(variable.getTrait().getTraitDbId()));
		property.setName(variable.getTrait().getName());
		property.setDefinition(variable.getTrait().getDescription());
		this.ontologyPropertyDataManager.updateProperty(property);

		final Method method = new Method();
		method.setId(Integer.valueOf(variable.getMethod().getMethodDbId()));
		method.setName(variable.getMethod().getName());
		method.setDefinition(variable.getMethod().getDescription());
		this.ontologyMethodDataManager.updateMethod(method);

		// TODO: Update Scale
		//		final Scale scale = new Scale();
		//		scale.setId(Integer.valueOf(variable.getScale().getScaleDbId()));
		//		scale.setName(variable.getScale().getName());
		//		scale.setDataType(DataType.getByBrapiName(variable.getScale().getDataType()));
		//		for (final CategoryDTO categoryDTO : variable.getScale().getValidValues().getCategories()) {
		//			scale.addCategory(new TermSummary(null, categoryDTO.getValue(), categoryDTO.getLabel()));
		//		}
		//		this.ontologyScaleDataManager.updateScale(scale);

		if (!CollectionUtils.isEmpty(variable.getStudyDbIds())) {
			for (final String studyDbId : variable.getStudyDbIds()) {
				this.addObservationVariableToStudy(Integer.valueOf(studyDbId), Integer.valueOf(variable.getObservationVariableDbId()),
					variable.getObservationVariableName());
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
