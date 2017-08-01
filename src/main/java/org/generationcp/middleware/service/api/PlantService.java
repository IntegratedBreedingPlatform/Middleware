package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.plant.PlantDTO;

public interface PlantService {

	public Integer createPlant(PlantDTO plant);

	public PlantDTO getPlant(Integer plantId);

}
