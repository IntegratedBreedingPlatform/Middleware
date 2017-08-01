
package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.dao.PlantDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.plant.PlantDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Plant;
import org.generationcp.middleware.service.api.PlantService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional
public class PlantServiceImpl implements PlantService {

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
	public Integer createPlant(PlantDTO plantDTO) {
		Plant plant = new Plant();

		plant.setCreatedDate(new Date());
		plant.setExperiment(this.experimentDao.getById(plantDTO.getExperiment().getId()));
		plant.setPlantBusinessKey(plantDTO.getPlantBusinessKey());
		plant.setPlantNumber(plantDTO.getPlantNumber());
		this.plantDao.saveOrUpdate(plant);
		return plant.getPlantId();
	}

	public PlantDTO getPlant(Integer plantId){
		Plant plant = this.plantDao.getById(plantId);
		PlantDTO plantDTO = new PlantDTO();

		Experiment experiment = new Experiment();
		experiment.setId(plant.getExperiment().getNdExperimentId());
		plantDTO.setExperiment(experiment);
		plantDTO.setCreatedDate(plant.getCreatedDate());
		plantDTO.setPlantBusinessKey(plant.getPlantBusinessKey());
		plantDTO.setPlantId(plant.getPlantId());
		plantDTO.setPlantNumber(plant.getPlantNumber());
		return plantDTO;
	}
}
