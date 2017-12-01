package org.generationcp.middleware.data.initializer;

import java.util.Date;

import org.generationcp.middleware.pojos.Plant;
import org.generationcp.middleware.pojos.dms.ExperimentModel;

public class PlantTestDataInitializer {
	public static Plant createPlant() {
		final Plant plant = new Plant();
		plant.setCreatedDate(new Date());
		plant.setExperiment(new ExperimentModel());
		plant.setPlantBusinessKey("PABCD");
		plant.setPlantNumber(0);
		return plant;
	}
}
