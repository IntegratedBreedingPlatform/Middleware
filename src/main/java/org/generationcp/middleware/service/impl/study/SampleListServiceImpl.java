package org.generationcp.middleware.service.impl.study;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.samplelist.SampleListDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.service.api.SampleListService;
import org.generationcp.middleware.service.api.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional public class SampleListServiceImpl implements SampleListService {

	private SampleListDao sampleListDao;

	private SampleDao sampleDao;

	private UserDAO userDao;

	@Autowired private SampleService sampleService;

	public SampleListServiceImpl(HibernateSessionProvider sessionProvider) {
		this.sampleListDao = new SampleListDao();
		this.sampleListDao.setSession(sessionProvider.getSession());
		this.sampleDao = new SampleDao();
		this.sampleDao.setSession(sessionProvider.getSession());
		this.userDao = new UserDAO();
		this.userDao.setSession(sessionProvider.getSession());

	}

	@Override public Integer createOrUpdateSampleList(SampleListDTO sampleListDTO) {
		SampleList sampleList = new SampleList();

		sampleList.setCreatedDate(new Date());
		sampleList.setCreatedBy(userDao.getUserByUserName(sampleListDTO.getCreatedBy().getUsername()));
		sampleList.setDescription(sampleListDTO.getDescription());
		sampleList.setHierarchy(sampleListDao.getBySampleListId(sampleListDTO.getHierarchy().getListId()));
		sampleList.setListName(sampleListDTO.getListName());
		sampleList.setNotes(sampleListDTO.getNotes());
		sampleList.setType(sampleListDTO.getType());

		List<Sample> samples = new ArrayList<>();
		Iterator<SampleDTO> samplesIterator = sampleListDTO.getSamples().iterator();
		while (samplesIterator.hasNext()) {
			samples.add(sampleDao.getBySampleId(sampleService.createOrUpdateSample(samplesIterator.next())));
		}

		this.sampleListDao.saveOrUpdate(sampleList);
		return sampleList.getListId();
	}

	@Override public SampleListDTO getSampleList(Integer sampleListId) {
		SampleList sampleList = this.sampleListDao.getById(sampleListId);
		SampleListDTO sampleListDTO = new SampleListDTO();

		sampleListDTO.setCreatedBy(userDao.mapUserToUserDto(sampleList.getCreatedBy()));
		sampleListDTO.setDescription(sampleList.getDescription());
		sampleListDTO.setHierarchy(this.getHierarchy(sampleList.getHierarchy()));
		sampleListDTO.setListName(sampleList.getListName());
		sampleListDTO.setNotes(sampleList.getNotes());
		sampleListDTO.setType(sampleList.getType());
		sampleListDTO.setCreatedDate(sampleList.getCreatedDate());

		final Collection<Integer> sampleIds = CollectionUtils.collect(sampleList.getSamples(), new Transformer() {

			@Override public Object transform(final Object input) {
				final Sample sample = (Sample) input;
				return sample.getSampleId();
			}
		});

		sampleListDTO.setSamples(sampleService.getSamples(sampleIds));
		return sampleListDTO;
	}

	private SampleListDTO getHierarchy(SampleList hierarchy) {
		if (hierarchy != null) {
			return this.getSampleList(hierarchy.getListId());
		}
		return null;
	}
}
