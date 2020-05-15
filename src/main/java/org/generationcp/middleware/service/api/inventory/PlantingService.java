package org.generationcp.middleware.service.api.inventory;

import org.generationcp.middleware.domain.inventory.planting.PlantingMetadata;
import org.generationcp.middleware.domain.inventory.planting.PlantingRequestDto;

public interface PlantingService {

	PlantingMetadata getPlantingMetadata(PlantingRequestDto plantingRequestDto);

}
