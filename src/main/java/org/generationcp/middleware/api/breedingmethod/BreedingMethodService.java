package org.generationcp.middleware.api.breedingmethod;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BreedingMethodService {

	List<MethodClassDTO> getMethodClasses();

	BreedingMethodDTO getBreedingMethod(Integer breedingMethodDbId);

	List<BreedingMethodDTO> getBreedingMethods(BreedingMethodSearchRequest methodSearchRequest, Pageable pageable);

	Long countBreedingMethods(BreedingMethodSearchRequest methodSearchRequest);
}
