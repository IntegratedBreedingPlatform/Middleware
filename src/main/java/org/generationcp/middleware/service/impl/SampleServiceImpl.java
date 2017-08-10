package org.generationcp.middleware.service.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.dao.PlantDao;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.service.api.PlantService;
import org.generationcp.middleware.service.api.SampleListService;
import org.generationcp.middleware.service.api.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional
public class SampleServiceImpl implements SampleService {

	private static final String S = "S";

	private SampleDao sampleDao;
	private ExperimentDao experimentDao;
	private PlantDao plantDao;
	private UserDAO userDao;

	@Autowired private WorkbenchDataManager workbenchDataManager;

	@Autowired private SampleListService sampleListService;

	@Autowired private PlantService plantService;

	public SampleServiceImpl(HibernateSessionProvider sessionProvider) {
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
	public Sample createOrUpdateSample(String cropName, Integer plantNumber, String username, String sampleName, Date samplingDate,
		Integer experimentId, SampleList sampleList) {

		Sample sample = new Sample();
		String cropPrefix = this.workbenchDataManager.getCropTypeByName(cropName).getPlotCodePrefix();
		sample.setPlant(plantService.createOrUpdatePlant(cropPrefix, plantNumber, experimentId));

		if (!username.isEmpty()) {
			sample.setTakenBy(userDao.getUserByUserName(username));
		}

		sample.setSampleName(sampleName);//Preferred name GID
		sample.setCreatedDate(new Date());
		sample.setSamplingDate(samplingDate);
		sample.setSampleBusinessKey(this.getSampleBusinessKey(cropPrefix));
		sample.setSampleList(sampleList);

		return sample;
	}

	private String getSampleBusinessKey(String cropPrefix) {
		String sampleBussinesKey = cropPrefix;
		sampleBussinesKey = sampleBussinesKey + S;
		sampleBussinesKey = sampleBussinesKey + RandomStringUtils.randomAlphanumeric(8);

		return sampleBussinesKey;
	}

}
