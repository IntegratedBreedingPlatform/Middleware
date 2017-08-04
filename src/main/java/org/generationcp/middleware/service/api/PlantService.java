package org.generationcp.middleware.service.api;

import org.generationcp.middleware.pojos.Plant;

public interface PlantService {

	public Plant createOrUpdatePlant(String cropPrefix, Integer plantNumber, Integer experimentId);



}
