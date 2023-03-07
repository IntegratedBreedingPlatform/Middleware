package org.generationcp.middleware.api.genotype;

import org.generationcp.middleware.domain.genotype.GenotypeDTO;
import org.generationcp.middleware.domain.genotype.GenotypeImportRequestDto;
import org.generationcp.middleware.domain.genotype.GenotypeSearchRequestDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenotypeService {

    List<Integer> importGenotypes(final List<GenotypeImportRequestDto> genotypeImportRequestDtos);

    List<GenotypeDTO> searchGenotypes(GenotypeSearchRequestDTO searchRequestDTO, Pageable pageable);

    long countGenotypes(GenotypeSearchRequestDTO searchRequestDTO);

    long countFilteredGenotypes(GenotypeSearchRequestDTO searchRequestDTO);
}
