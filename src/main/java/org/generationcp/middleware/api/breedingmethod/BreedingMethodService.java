package org.generationcp.middleware.api.breedingmethod;

import java.util.List;
import java.util.Set;

public interface BreedingMethodService {

	List<MethodClassDTO> getMethodClasses();

	BreedingMethodDTO getBreedingMethod(Integer breedingMethodDbId);

	List<BreedingMethodDTO> getBreedingMethods(BreedingMethodSearchRequest methodSearchRequest);

	List<BreedingMethodDTO> getAllBreedingMethods();
}
