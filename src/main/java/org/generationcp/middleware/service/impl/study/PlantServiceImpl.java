
package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Plant;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.service.api.PlantService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
@Transactional
public class PlantServiceImpl implements PlantService {

	private static final String L = "L";

	public PlantServiceImpl(final HibernateSessionProvider sessionProvider) {
	}

	@Override
	public Plant buildPlant(final String cropPrefix, final Integer plantNumber, final Integer experimentId) {
		final Plant plant = new Plant();

		plant.setCreatedDate(new Date());
		plant.setExperiment(new ExperimentModel(experimentId));
		plant.setPlantBusinessKey(this.getPlantBusinessKey(cropPrefix));
		plant.setPlantNumber(plantNumber);

		return plant;
	}

	private String getPlantBusinessKey(final String cropPrefix) {
		String plantBussinesKey = cropPrefix;
		plantBussinesKey = plantBussinesKey + PlantServiceImpl.L;
		plantBussinesKey = plantBussinesKey + RandomStringUtils.randomAlphanumeric(8);

		return plantBussinesKey;
	}
}
