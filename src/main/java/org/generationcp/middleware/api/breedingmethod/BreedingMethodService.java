package org.generationcp.middleware.api.breedingmethod;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BreedingMethodService {

	List<MethodClassDTO> getMethodClasses();

	Optional<BreedingMethodDTO> getBreedingMethod(Integer breedingMethodDbId);

	BreedingMethodDTO create(BreedingMethodNewRequest breedingMethod);

	BreedingMethodDTO edit(Integer breedingMethodDbId, BreedingMethodNewRequest breedingMethod);

	void delete(Integer breedingMethodDbId);

	List<BreedingMethodDTO> getBreedingMethods(BreedingMethodSearchRequest methodSearchRequest, Pageable pageable);

	Long countBreedingMethods(BreedingMethodSearchRequest methodSearchRequest);
}
