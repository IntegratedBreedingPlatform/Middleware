package org.generationcp.middleware.api.genotype;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import org.generationcp.middleware.api.user.UserSearchRequest;
import org.generationcp.middleware.dao.GenotypeDao;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.genotype.SampleGenotypeDTO;
import org.generationcp.middleware.domain.genotype.SampleGenotypeImportRequestDto;
import org.generationcp.middleware.domain.genotype.SampleGenotypeSearchRequestDTO;
import org.generationcp.middleware.domain.genotype.SampleGenotypeVariablesSearchFilter;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Genotype;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

@Service
@Transactional
public class SampleGenotypeServiceImpl implements SampleGenotypeService {

	@Resource
	private StudyService studyService;

	@Resource
	private DatasetService datasetService;

	@Resource
	private OntologyVariableService ontologyVariableService;

	@Autowired
	private UserService userService;

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
	public List<SampleGenotypeDTO> searchSampleGenotypes(final SampleGenotypeSearchRequestDTO searchRequestDTO, final Pageable pageable) {
		this.updateSearchDTO(searchRequestDTO);
		if (!CollectionUtils.isEmpty(searchRequestDTO.getSampleGenotypeVariables())) {
			this.updateTakenByIds(searchRequestDTO);
			final List<SampleGenotypeDTO> sampleGenotypeDTOS = this.daoFactory.getGenotypeDao().searchGenotypes(searchRequestDTO, pageable);
			this.populateTakenBy(sampleGenotypeDTOS);
			return sampleGenotypeDTOS;
		}
		return new ArrayList<>();
	}

	@Override
	public long countSampleGenotypes(final SampleGenotypeSearchRequestDTO searchRequestDTO) {
		return this.daoFactory.getGenotypeDao().countGenotypes(searchRequestDTO);
	}

	@Override
	public long countFilteredSampleGenotypes(final SampleGenotypeSearchRequestDTO searchRequestDTO) {
		this.updateTakenByIds(searchRequestDTO);
		return this.daoFactory.getGenotypeDao().countFilteredGenotypes(searchRequestDTO);
	}

	@Override
	public long countSampleGenotypesBySampleList(final Integer listId) {
		return this.daoFactory.getGenotypeDao().countSampleGenotypesBySampleList(listId);
	}

	@Override
	public Map<Integer, MeasurementVariable> getSampleGenotypeVariables(final SampleGenotypeVariablesSearchFilter filter) {

		Preconditions.checkNotNull(filter.getStudyId());
		final List<Integer> sampleGenotypeVariables = this.daoFactory.getGenotypeDao().getSampleGenotypeVariableIds(filter);

		final Map<Integer, MeasurementVariable> result = new HashMap<>();

		if (!CollectionUtils.isEmpty(sampleGenotypeVariables)) {

			final VariableFilter variableFilter = new VariableFilter();
			sampleGenotypeVariables.forEach(variableFilter::addVariableId);
			this.ontologyVariableService.getVariablesWithFilterById(variableFilter).values().forEach(v -> {
				final MeasurementVariable measurementVariable = new MeasurementVariable();
				measurementVariable.setTermId(v.getId());
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
		}

		return result;
	}

	@Override
	public List<MeasurementVariable> getSampleGenotypeColumns(final Integer studyId, final List<Integer> sampleListIds) {
		final Integer plotDatasetId = this.studyService.getPlotDatasetId(studyId);
		final List<MeasurementVariable> variables = this.datasetService.getObservationSetVariables(plotDatasetId,
				Lists.newArrayList(VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.ENTRY_DETAIL.getId(),
					VariableType.GERMPLASM_DESCRIPTOR.getId(), VariableType.EXPERIMENTAL_DESIGN.getId()))
			.stream().filter(var -> GenotypeDao.STANDARD_SAMPLE_GENOTYPE_TABLE_VARIABLE_IDS.contains(var.getTermId()))
			.collect(Collectors.toList());
		this.addSampleColumns(variables);

		final SampleGenotypeVariablesSearchFilter sampleGenotypeVariablesSearchFilter = new SampleGenotypeVariablesSearchFilter();
		sampleGenotypeVariablesSearchFilter.setSampleListIds(sampleListIds);
		sampleGenotypeVariablesSearchFilter.setStudyId(studyId);
		variables.addAll(this.getSampleGenotypeVariables(sampleGenotypeVariablesSearchFilter).values());
		return variables;
	}

	private void updateTakenByIds(final SampleGenotypeSearchRequestDTO searchRequestDTO) {
		final UserSearchRequest userSearchRequest = new UserSearchRequest();
		if (searchRequestDTO.getFilter() !=null && MapUtils.isNotEmpty(searchRequestDTO.getFilter().getFilteredTextValues())
			&& searchRequestDTO.getFilter().getFilteredTextValues().containsKey(String.valueOf(TermId.TAKEN_BY.getId()))) {
			userSearchRequest.setFullName(searchRequestDTO.getFilter().getFilteredTextValues().get(String.valueOf(TermId.TAKEN_BY.getId())));
			final List<UserDto> userDtos = this.userService.searchUsers(userSearchRequest, null);
			searchRequestDTO.setTakenByIds(userDtos.stream().map(UserDto::getId).collect(Collectors.toList()));
		}
	}

	public void addSampleColumns(final List<MeasurementVariable> variables) {
		variables.add(this.addTermIdColumn(TermId.SAMPLE_NAME));
		variables.add(this.addTermIdColumn(TermId.SAMPLE_UUID));
		final MeasurementVariable samplingDateVariable = this.addTermIdColumn(TermId.SAMPLING_DATE);
		samplingDateVariable.setDataTypeId(DataType.DATE_TIME_VARIABLE.getId());
		samplingDateVariable.setDataType(DataType.DATE_TIME_VARIABLE.getName());
		variables.add(samplingDateVariable);
		variables.add(this.addTermIdColumn(TermId.TAKEN_BY));
	}

	private void updateSearchDTO(final SampleGenotypeSearchRequestDTO searchRequestDTO) {

		final SampleGenotypeVariablesSearchFilter filter = new SampleGenotypeVariablesSearchFilter();
		filter.setStudyId(searchRequestDTO.getStudyId());
		if (searchRequestDTO.getFilter().getDatasetId() != null) {
			filter.setDatasetIds(Arrays.asList());
		}
		filter.setSampleListIds(searchRequestDTO.getFilter().getSampleListIds());

		// Add the list of Marker variables available in study
		searchRequestDTO.setSampleGenotypeVariables(new ArrayList<>(
			this.getSampleGenotypeVariables(filter).values()));
	}

	void populateTakenBy(final List<SampleGenotypeDTO> sampleGenotypeDTOS) {
		// Populate takenBy with full name of user from workbench database.
		final List<Integer> userIds = sampleGenotypeDTOS.stream().map(SampleGenotypeDTO::getTakenById).collect(Collectors.toList());
		final Map<Integer, String> userIDFullNameMap = this.userService.getUserIDFullNameMap(userIds);
		sampleGenotypeDTOS.forEach(
			sampleGenotypeDTO -> sampleGenotypeDTO.setTakenBy(userIDFullNameMap.get(sampleGenotypeDTO.getTakenById())));
	}

	private MeasurementVariable addTermIdColumn(final TermId termId) {
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setName(termId.name());
		measurementVariable.setAlias(termId.name());
		measurementVariable.setTermId(termId.getId());
		measurementVariable.setVariableType(null);
		measurementVariable.setFactor(true);
		return measurementVariable;
	}
}
