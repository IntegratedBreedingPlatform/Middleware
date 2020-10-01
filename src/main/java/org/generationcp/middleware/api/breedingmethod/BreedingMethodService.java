package org.generationcp.middleware.api.breedingmethod;

import java.util.List;
import java.util.Map;

public interface BreedingMethodService {

	Map<String, List<MethodClassDTO>> getMethodClasses();

	BreedingMethodDTO getBreedingMethod(Integer breedingMethodDbId);
}
