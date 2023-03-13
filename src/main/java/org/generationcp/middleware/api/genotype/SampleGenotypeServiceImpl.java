package org.generationcp.middleware.api.genotype;

import com.google.common.base.Preconditions;
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import org.generationcp.middleware.dao.GenotypeDao;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.genotype.GenotypeDTO;
import org.generationcp.middleware.domain.genotype.SampleGenotypeImportRequestDto;
import org.generationcp.middleware.domain.genotype.SampleGenotypeSearchRequestDTO;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Genotype;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SampleGenotypeServiceImpl implements SampleGenotypeService {

	private final DaoFactory daoFactory;

	@Resource
	private OntologyVariableService ontologyVariableService;

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
		final Map<Integer, MeasurementVariable> genotypeVariablesMap =
			this.getSampleGenotypeVariables(searchRequestDTO.getStudyId(),
				searchRequestDTO.getFilter() != null ? searchRequestDTO.getFilter().getDatasetId() : null);
		if (!CollectionUtils.isEmpty(genotypeVariablesMap)) {
			final List<String> genotypeVariableNames =
				genotypeVariablesMap.values().stream().map(MeasurementVariable::getName).collect(
					Collectors.toList());
			return this.daoFactory.getGenotypeDao().searchGenotypes(searchRequestDTO, genotypeVariableNames, pageable);
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
}
