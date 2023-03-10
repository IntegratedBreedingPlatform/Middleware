package org.generationcp.middleware.api.genotype;

import org.generationcp.middleware.domain.genotype.GenotypeDTO;
import org.generationcp.middleware.domain.genotype.SampleGenotypeImportRequestDto;
import org.generationcp.middleware.domain.genotype.SampleGenotypeSearchRequestDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SampleGenotypeService {

    List<Integer> importSampleGenotypes(final List<SampleGenotypeImportRequestDto> sampleGenotypeImportRequestDtos);

    List<GenotypeDTO> searchSampleGenotypes(SampleGenotypeSearchRequestDTO searchRequestDTO, Pageable pageable);

    long countSampleGenotypes(SampleGenotypeSearchRequestDTO searchRequestDTO);

    long countFilteredSampleGenotypes(SampleGenotypeSearchRequestDTO searchRequestDTO);
}
