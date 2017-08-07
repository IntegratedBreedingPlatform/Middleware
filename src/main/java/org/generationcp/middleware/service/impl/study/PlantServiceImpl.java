
package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.dao.PlantDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Plant;
import org.generationcp.middleware.service.api.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional
public class PlantServiceImpl implements PlantService {

	private static final String L = "L";


	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	private PlantDao plantDao;
	private ExperimentDao experimentDao;

	public PlantServiceImpl() {

	}

	public PlantServiceImpl(HibernateSessionProvider sessionProvider) {
		this.plantDao = new PlantDao();
		this.plantDao.setSession(sessionProvider.getSession());

		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(sessionProvider.getSession());
	}

	public PlantServiceImpl(PlantDao plantDao, ExperimentDao experimentDao) {
		this.plantDao = plantDao;
		this.experimentDao = experimentDao;
	}

	@Override
	public Plant createOrUpdatePlant(String cropPrefix, Integer plantNumber, Integer experimentId) {
		Plant plant = new Plant();

		plant.setCreatedDate(new Date());
		plant.setExperiment(experimentDao.getById(experimentId));
		plant.setPlantBusinessKey(this.getPlantBusinessKey(cropPrefix));
		plant.setPlantNumber(plantNumber);

		return plant;
	}

	private String getPlantBusinessKey(String cropPrefix) {
		String plantBussinesKey = cropPrefix;
		plantBussinesKey = plantBussinesKey + L;
		plantBussinesKey = plantBussinesKey + RandomStringUtils.randomAlphanumeric(8);

		return plantBussinesKey;
	}
}
