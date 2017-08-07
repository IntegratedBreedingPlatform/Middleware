package org.generationcp.middleware.service.impl.study;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.generationcp.middleware.dao.PlantDao;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.domain.samplelist.SampleListDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.service.api.SampleListService;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Transactional
public class SampleListServiceImpl implements SampleListService {

	private SampleListDao sampleListDao;

	private SampleDao sampleDao;

	private UserDAO userDao;

	private StudyMeasurements studyMeasurements;

	@Autowired private SampleService sampleService;
	private PlantDao plantDao;

	public SampleListServiceImpl(HibernateSessionProvider sessionProvider) {
		this.sampleListDao = new SampleListDao();
		this.sampleListDao.setSession(sessionProvider.getSession());
		this.sampleDao = new SampleDao();
		this.sampleDao.setSession(sessionProvider.getSession());
		this.userDao = new UserDAO();
		this.userDao.setSession(sessionProvider.getSession());
		this.plantDao = new PlantDao();
		this.plantDao.setSession(sessionProvider.getSession());
		this.studyMeasurements = new StudyMeasurements(sessionProvider.getSession());
	}

	@Override public Integer createOrUpdateSampleList(SampleListDTO sampleListDTO) {
		SampleList sampleList = new SampleList();

		sampleList.setCreatedDate(new Date());
		sampleList.setCreatedBy(userDao.getUserByUserName(sampleListDTO.getCreatedBy()));
		sampleList.setDescription(sampleListDTO.getDescription());
		sampleList.setListName(sampleListDTO.getTrialName() + "#" + Util.getCurrentDateAsStringValue("yyyyMMddHHmmssSSS"));
		sampleList.setNotes(sampleListDTO.getNotes());
		sampleList.setType(sampleListDTO.getType());

		List<ObservationDto> observationDtos = studyMeasurements
			.getSampleObservations(sampleListDTO.getStudyId(), sampleListDTO.getInstanceIds(), sampleListDTO.getSelectionVariableId());

		Map<Integer, Integer> maxPlantNumbers = this.getMaxPlantNumber(observationDtos);
		List<Sample> samples = new ArrayList<>();
		Iterator<ObservationDto> observationsIterator = observationDtos.iterator();

		while (observationsIterator.hasNext()) {

			ObservationDto observationDto = observationsIterator.next();
			Integer sampleNumber = new Integer(observationDto.getVariableMeasurements().get(0).getVariableValue());
			Integer count = maxPlantNumbers.get(observationDto.getMeasurementId());
			if (count == null) {
				//counter should be start in 1
				count = 0;
			}
			for (int i = 0; i < sampleNumber; i++) {
				count++;
				Sample sample = sampleService
					.createOrUpdateSample(sampleListDTO.getCropName(), count, sampleListDTO.getTakenBy(), observationDto.getDesignation(),
						sampleListDTO.getSamplingDate(), observationDto.getMeasurementId());
				samples.add(sample);
			}
		}

		sampleList.setSamples(samples);
		this.sampleListDao.saveOrUpdate(sampleList);
		return sampleList.getListId();
	}

	private Map<Integer, Integer> getMaxPlantNumber(List<ObservationDto> observationDtos) {

		final Collection<Integer> experimentIds = CollectionUtils.collect(observationDtos, new Transformer() {

			@Override public Object transform(final Object input) {
				final ObservationDto observationDto = (ObservationDto) input;
				return observationDto.getMeasurementId();
			}
		});

		return this.plantDao.getMaxPlantNumber(experimentIds);
	}
}
