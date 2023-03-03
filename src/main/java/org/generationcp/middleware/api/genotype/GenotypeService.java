package org.generationcp.middleware.api.genotype;

import org.generationcp.middleware.domain.genotype.GenotypeImportRequestDto;

import java.util.List;

public interface GenotypeService {

    List<Integer> importGenotypes(final List<GenotypeImportRequestDto> genotypeImportRequestDtos);
}
