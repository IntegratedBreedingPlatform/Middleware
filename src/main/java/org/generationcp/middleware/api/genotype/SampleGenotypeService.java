package org.generationcp.middleware.api.genotype;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.genotype.SampleGenotypeDTO;
import org.generationcp.middleware.domain.genotype.SampleGenotypeImportRequestDto;
import org.generationcp.middleware.domain.genotype.SampleGenotypeSearchRequestDTO;
import org.generationcp.middleware.domain.genotype.SampleGenotypeVariablesSearchFilter;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface SampleGenotypeService {

	List<Integer> importSampleGenotypes(List<SampleGenotypeImportRequestDto> sampleGenotypeImportRequestDtos);

	List<SampleGenotypeDTO> searchSampleGenotypes(SampleGenotypeSearchRequestDTO searchRequestDTO, Pageable pageable);

	long countFilteredSampleGenotypes(SampleGenotypeSearchRequestDTO searchRequestDTO);

	long countSampleGenotypesBySampleList(Integer listId);

	Map<Integer, MeasurementVariable> getSampleGenotypeVariables(SampleGenotypeVariablesSearchFilter filter);

	List<MeasurementVariable> getSampleGenotypeColumns(Integer studyId, List<Integer> sampleListIds);
}
