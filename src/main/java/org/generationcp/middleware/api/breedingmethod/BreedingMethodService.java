package org.generationcp.middleware.api.breedingmethod;

import java.util.List;
import java.util.Set;

public interface BreedingMethodService {

	List<MethodClassDTO> getMethodClasses();

	BreedingMethodDTO getBreedingMethod(Integer breedingMethodDbId);

	List<BreedingMethodDTO> getBreedingMethodsByCodes(Set<String> breedingMethodCodes);

	List<BreedingMethodDTO> getBreedingMethods(String programUUID, Set<String> abbreviations, boolean favorites);
}
