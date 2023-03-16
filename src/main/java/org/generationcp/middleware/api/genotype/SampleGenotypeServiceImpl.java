package org.generationcp.middleware.api.genotype;

import com.google.common.base.Preconditions;
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import com.google.common.collect.Lists;
import org.generationcp.middleware.dao.GenotypeDao;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.genotype.GenotypeDTO;
import org.generationcp.middleware.domain.genotype.SampleGenotypeImportRequestDto;
import org.generationcp.middleware.domain.genotype.SampleGenotypeSearchRequestDTO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Genotype;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.StudyService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SampleGenotypeServiceImpl implements SampleGenotypeService {

	@Resource
	private StudyService studyService;

	@Resource
	private DatasetService datasetService;

	@Resource
	private OntologyVariableService ontologyVariableService;

	private final DaoFactory daoFactory;

	public SampleGenotypeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<Integer> importSampleGenotypes(final List<SampleGenotypeImportRequestDto> sampleGenotypeImportRequestDtos) {
		final List<Integer> genotypeIds = new ArrayList<>();
		final GenotypeDao genotypeDao = this.daoFactory.getGenotypeDao();
		for (final SampleGenotypeImportRequestDto importRequestDto : sampleGenotypeImportRequestDtos) {
			final Genotype genotype = new Genotype();
			genotype.setSample(new Sample(Integer.valueOf(importRequestDto.getSampleId())));
			final CVTerm variable = new CVTerm();
			variable.setCvTermId(Integer.valueOf(importRequestDto.getVariableId()));
			genotype.setVariable(variable);
			genotype.setValue(importRequestDto.getValue());
			genotypeDao.save(genotype);
			genotypeIds.add(genotype.getId());
		}
		return genotypeIds;
	}

	@Override
	public List<GenotypeDTO> searchSampleGenotypes(final SampleGenotypeSearchRequestDTO searchRequestDTO, final Pageable pageable) {
		this.updateSearchDTO(searchRequestDTO);
		if (!CollectionUtils.isEmpty(searchRequestDTO.getSampleGenotypeVariableDTOs())) {
			return this.daoFactory.getGenotypeDao().searchGenotypes(searchRequestDTO, pageable);
		}
		return new ArrayList<>();
	}

	@Override
	public long countSampleGenotypes(final SampleGenotypeSearchRequestDTO searchRequestDTO) {
		return this.daoFactory.getGenotypeDao().countGenotypes(searchRequestDTO);
	}

	@Override
	public long countFilteredSampleGenotypes(final SampleGenotypeSearchRequestDTO searchRequestDTO) {
		return this.daoFactory.getGenotypeDao().countFilteredGenotypes(searchRequestDTO);
	}

	@Override
	public long countSampleGenotypesBySampleList(final Integer listId) {
		return this.daoFactory.getSampleListDao().countSampleGenotypesBySampleList(listId);
	}

	@Override
	public Map<Integer, MeasurementVariable> getSampleGenotypeVariables(final Integer studyId, final Integer datasetId) {
		Preconditions.checkNotNull(studyId);
		final List<Integer> sampleGenotypeVariables = this.daoFactory.getCvTermDao().getSampleGenotypeVariableIds(studyId, datasetId);

		final Map<Integer, MeasurementVariable> result = new HashMap<>();

		final VariableFilter variableFilter = new VariableFilter();
		sampleGenotypeVariables.forEach(variableFilter::addVariableId);
		this.ontologyVariableService.getVariablesWithFilterById(variableFilter).values().forEach(v -> {
			final MeasurementVariable measurementVariable = new MeasurementVariable();
			measurementVariable.setName(v.getName());
			measurementVariable.setAlias(StringUtils.isEmpty(v.getAlias()) ? v.getName() : v.getAlias());
			measurementVariable.setDescription(v.getDefinition());
			measurementVariable.setVariableType(VariableType.GENOTYPE_MARKER);
			measurementVariable.setProperty(v.getProperty().getName());
			measurementVariable.setScale(v.getScale().getName());
			measurementVariable.setMethod(v.getMethod().getName());
			measurementVariable.setDataType(v.getScale().getDataType().getName());
			measurementVariable.setDataTypeId(v.getScale().getDataType().getId());
			result.put(v.getId(), measurementVariable);
		});

		return result;
	}

	private void updateSearchDTO(final SampleGenotypeSearchRequestDTO searchRequestDTO) {
		final List<Integer> fixedEntryDetailVariableIds = Arrays.asList(TermId.ENTRY_NO.getId(), TermId.ENTRY_TYPE.getId());
		final Integer datasetId = searchRequestDTO.getFilter() == null ? null : searchRequestDTO.getFilter().getDatasetId();
		final List<Integer> sampleGenotypeVariables = this.daoFactory.getCvTermDao()
				.getSampleGenotypeVariableIds(searchRequestDTO.getStudyId(), datasetId);

		final List<MeasurementVariableDto> sampleGenotypeMeasurementVariableDTOs = new ArrayList<>();
		final List<MeasurementVariableDto> entryDetails = new ArrayList<>();
		final VariableFilter variableFilter = new VariableFilter();
		sampleGenotypeVariables.forEach(variableFilter::addVariableId);

		// Manually retrieve ENTRY_NO and ENTRY_TYPE for now. Additional Entry Detail Variables maybe added in the future
		variableFilter.addVariableIds(fixedEntryDetailVariableIds);
		this.ontologyVariableService.getVariablesWithFilterById(variableFilter).values().forEach(v -> {
			if (fixedEntryDetailVariableIds.contains(v.getId())) {
				entryDetails.add(new MeasurementVariableDto(v.getId(), v.getName()));
			} else {
				sampleGenotypeMeasurementVariableDTOs.add(new MeasurementVariableDto(v.getId(), v.getName()));
			}
		});
		searchRequestDTO.setSampleGenotypeVariableDTOs(sampleGenotypeMeasurementVariableDTOs);
		searchRequestDTO.setEntryDetails(entryDetails);
	}

	public List<MeasurementVariable> getSampleGenotypeColumns(final Integer studyId) {
		final Integer plotDatasetId = this.studyService.getPlotDatasetId(studyId);

		final List<MeasurementVariable> variables =
				this.datasetService.getObservationSetVariables(plotDatasetId,
						Lists.newArrayList(VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.ENTRY_DETAIL.getId(),
								VariableType.GERMPLASM_DESCRIPTOR.getId(), VariableType.EXPERIMENTAL_DESIGN.getId()));
		variables.addAll(this.getSampleGenotypeVariables(studyId, null).values());
		return variables;
	}
}
