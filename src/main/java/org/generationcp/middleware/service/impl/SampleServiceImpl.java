package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.dao.PlantDao;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.service.api.PlantService;
import org.generationcp.middleware.service.api.SampleListService;
import org.generationcp.middleware.service.api.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Transactional public class SampleServiceImpl implements SampleService {

	private SampleDao sampleDao;
	private ExperimentDao experimentDao;
	private PlantDao plantDao;
	private UserDAO userDao;

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

	@Override public Integer createOrUpdateSample(SampleDTO sampleDTO) {

		Sample sample = new Sample();

		sample.setPlant(plantDao.getById(plantService.createPlant(sampleDTO.getPlant())));
		sample.setTakenBy(userDao.getUserByUserName(sampleDTO.getTakenBy().getUsername()));
		sample.setSampleName(sampleDTO.getSampleName());
		sample.setCreatedDate(sampleDTO.getCreatedDate());
		sample.setSamplingDate(sampleDTO.getSamplingDate());
		sample.setSampleBusinessKey(sampleDTO.getSampleBusinessKey());

		this.sampleDao.saveOrUpdate(sample);

		return sample.getSampleId();
	}

	@Override public SampleDTO getSample(Integer sampleId) {
		final Sample sample = this.sampleDao.getBySampleId(sampleId);

		SampleDTO sampleDTO = new SampleDTO();
		sampleDTO.setSampleId(sample.getSampleId());
		sampleDTO.setSampleName(sample.getSampleName());
		sampleDTO.setTakenBy(this.userDao.mapUserToUserDto(sample.getTakenBy()));
		sampleDTO.setSamplingDate(sample.getSamplingDate());
		sampleDTO.setCreatedDate(sample.getCreatedDate());
		sampleDTO.setSampleBusinessKey(sample.getSampleBusinessKey());
		sampleDTO.setPlant(this.plantService.getPlant(sample.getPlant().getPlantId()));
		sampleDTO.setSampleList(sampleListService.getSampleList(sample.getSampleList().getListId()));
		return sampleDTO;
	}

	@Override public List<SampleDTO> getSamples(Collection<Integer> sampleIds) {
		List<SampleDTO> samples = new ArrayList<>();
		Iterator<Integer> sampleIdsIterator = sampleIds.iterator();
		while (sampleIdsIterator.hasNext()) {
			samples.add(this.getSample(sampleIdsIterator.next()));
		}
		return samples;
	}

}
