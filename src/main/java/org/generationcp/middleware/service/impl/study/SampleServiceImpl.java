package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.dao.PlantDao;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.Plant;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.service.api.PlantService;
import org.generationcp.middleware.service.api.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Transactional
public class SampleServiceImpl implements SampleService {

	private static final String S = "S";

	private final SampleDao sampleDao;
	private final ExperimentDao experimentDao;
	private final PlantDao plantDao;
	private final UserDAO userDao;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private PlantService plantService;

	public SampleServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sampleDao = new SampleDao();
		this.sampleDao.setSession(sessionProvider.getSession());

		this.experimentDao = new ExperimentDao();
		this.experimentDao.setSession(sessionProvider.getSession());

		this.plantDao = new PlantDao();
		this.plantDao.setSession(sessionProvider.getSession());

		this.userDao = new UserDAO();
		this.userDao.setSession(sessionProvider.getSession());
	}

	@Override
	public Sample buildSample(final String cropName, final String cropPrefix, final Integer plantNumber, final String sampleName, final Date samplingDate, final Integer experimentId, final SampleList sampleList, User createdBy,
		Date createdDate, User takenBy) {

		final Sample sample = new Sample();
		String localCropPrefix;

		if (cropPrefix == null) {
			localCropPrefix = this.workbenchDataManager.getCropTypeByName(cropName).getPlotCodePrefix();
		} else {
			localCropPrefix = cropPrefix;
		}

		sample.setPlant(this.plantService.buildPlant(localCropPrefix, plantNumber, experimentId));
		sample.setTakenBy(takenBy);
		sample.setSampleName(sampleName);// Preferred name GID
		sample.setCreatedDate(new Date());
		sample.setSamplingDate(samplingDate);
		sample.setSampleBusinessKey(this.getSampleBusinessKey(cropPrefix));
		sample.setSampleList(sampleList);
		sample.setCreatedDate(createdDate);
		sample.setCreatedBy(createdBy);

		return sample;
	}

	private String getSampleBusinessKey(final String cropPrefix) {
		String sampleBussinesKey = cropPrefix;
		sampleBussinesKey = sampleBussinesKey + SampleServiceImpl.S;
		sampleBussinesKey = sampleBussinesKey + RandomStringUtils.randomAlphanumeric(8);

		return sampleBussinesKey;
	}

	@Override
	public List<SampleDTO> getSamples(final String plot_id) {
		final List<SampleDTO> listSampleDto = new ArrayList<>();
		final List<Sample> samples = this.sampleDao.getByPlotId(plot_id);
		for (Sample sample : samples) {
			SampleDTO dto = new SampleDTO();
			dto.setSampleName(sample.getSampleName());
			dto.setSampleBusinessKey(sample.getSampleBusinessKey());
			User takenBy = sample.getTakenBy();
			if (takenBy != null) {
				Person person = takenBy.getPerson();
				dto.setTakenBy(person.getFirstName() + " " + person.getLastName());
			}
			dto.setSamplingDate(sample.getSamplingDate());
			dto.setSampleList(sample.getSampleList().getListName());
			Plant plant = sample.getPlant();
			dto.setPlantNumber(plant.getPlantNumber());
			dto.setPlantBusinessKey(plant.getPlantBusinessKey());
			listSampleDto.add(dto);
		}
		return listSampleDto;
	}

}
