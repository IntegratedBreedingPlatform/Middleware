
package org.generationcp.middleware.service.impl.study;

import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.dao.PlantDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Plant;
import org.generationcp.middleware.service.api.PlantService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PlantServiceImpl implements PlantService {

	private static final String L = "L";

	private PlantDao plantDao;
	private ExperimentDao experimentDao;

	public PlantServiceImpl() {

	}

	public PlantServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.plantDao = new PlantDao();
		this.plantDao.setSession(sessionProvider.getSession());

		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(sessionProvider.getSession());
	}

	public PlantServiceImpl(final PlantDao plantDao, final ExperimentDao experimentDao) {
		this.plantDao = plantDao;
		this.experimentDao = experimentDao;
	}

	@Override
	public Plant buildPlant(final String cropPrefix, final Integer plantNumber, final Integer experimentId) {
		final Plant plant = new Plant();

		plant.setCreatedDate(new Date());
		plant.setExperiment(this.experimentDao.getById(experimentId));
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
