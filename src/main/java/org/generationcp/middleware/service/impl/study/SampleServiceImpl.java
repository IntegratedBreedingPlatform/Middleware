package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.PlantDao;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.Plant;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.service.api.PlantService;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Repository
@Transactional
public class SampleServiceImpl implements SampleService {

	private static final String S = "S";
	private static final SimpleDateFormat DATE_FORMAT = Util.getSimpleDateFormat(Util.FRONTEND_DATE_FORMAT_3);
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
		// Preferred name GID
		sample.setSampleName(sampleName);
		sample.setCreatedDate(new Date());
		sample.setSamplingDate(samplingDate);
		sample.setSampleBusinessKey(this.getSampleBusinessKey(cropPrefix));
		sample.setSampleList(sampleList);
		sample.setCreatedDate(createdDate);
		sample.setCreatedBy(createdBy);

		return sample;
	}

	private String getSampleBusinessKey(final String cropPrefix) {
		String sampleBusinessKey = cropPrefix;
		sampleBusinessKey = sampleBusinessKey + SampleServiceImpl.S;
		sampleBusinessKey = sampleBusinessKey + RandomStringUtils.randomAlphanumeric(8);

		return sampleBusinessKey;
	}

	@Override
	public List<SampleDTO> getSamples(final String plotId) {
		final List<Sample> samples = this.sampleDao.getByPlotId(plotId);
		return mapSampleToSampleDTO(samples);
	}

	private List<SampleDTO> mapSampleToSampleDTO(final List<Sample> samples) {
		final List<SampleDTO> listSampleDto = new ArrayList<>();
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

	public SampleDetailsDTO getSampleObservation(final String sampleId) {
		final SampleDetailsDTO samplesDetailsDto;
		final Sample sample = this.sampleDao.getBySampleBk(sampleId);

		return getSampleDetailsDTO(sample);
	}

	private SampleDetailsDTO getSampleDetailsDTO(final Sample sample) {
		final SampleDetailsDTO samplesDetailsDto;
		if (sample == null) {
			return new SampleDetailsDTO();
		}

		final ExperimentModel experiment = sample.getPlant().getExperiment();
		final DmsProject objectProject =
			experiment.getExperimentStocks().get(0).getExperiment().getProject().getRelatedTos().get(0).getObjectProject();
		final Integer studyId = objectProject.getProjectId();
		final String takenBy = sample.getTakenBy() != null ? sample.getTakenBy().getPerson().getDisplayName() : null;
		final String plotId = experiment.getPlotId();
		final String studyName = objectProject.getName();
		final StockModel stock = experiment.getExperimentStocks().get(0).getStock();
		final String entryNo = stock.getUniqueName();
		final Integer gid = stock.getDbxrefId();

		samplesDetailsDto = new SampleDetailsDTO(studyId, plotId, sample.getPlant().getPlantBusinessKey(), sample.getSampleBusinessKey());
		samplesDetailsDto.setTakenBy(takenBy);
		samplesDetailsDto.setSampleDate(sample.getSamplingDate());
		samplesDetailsDto.setStudyName(studyName);
		samplesDetailsDto.setEntryNo(Integer.valueOf(entryNo));
		samplesDetailsDto.setGid(gid);
		samplesDetailsDto.setSampleName(sample.getSampleName());
		samplesDetailsDto.setDesignation(stock.getName());
		samplesDetailsDto.setPlantNo(sample.getPlant().getPlantNumber());
		if (sample.getSamplingDate() != null) {
			samplesDetailsDto.setDisplayDate(DATE_FORMAT.format(sample.getSamplingDate()));
		}

		fillPlotNoByExperimentProperty(sample.getPlant().getExperiment().getProperties(), samplesDetailsDto);
		fillProjectProperties(sample.getPlant().getExperiment().getProject().getRelatedTos().get(0).getObjectProject().getProperties(),
			samplesDetailsDto);
		fillLocationByGeoLocationProperties(sample.getPlant().getExperiment().getGeoLocation().getProperties(), samplesDetailsDto);

		return samplesDetailsDto;
	}

	private void fillLocationByGeoLocationProperties(final List<GeolocationProperty> geolocationProperties,
		final SampleDetailsDTO samplesDetailsDto) {
		for (final GeolocationProperty properties : geolocationProperties) {
			if (properties.getTypeId().equals(TermId.TRIAL_LOCATION.getId()) && StringUtils.isNotBlank(properties.getValue())) {
				samplesDetailsDto.setLocationName(properties.getValue());
			} else if (properties.getTypeId().equals(TermId.LOCATION_ID.getId()) && StringUtils.isNotBlank(properties.getValue())) {
				samplesDetailsDto.setLocationDbId(Integer.valueOf(properties.getValue()));
			}
		}
	}

	private void fillProjectProperties(final List<ProjectProperty> projectProperties, final SampleDetailsDTO samplesDetailsDto) {

		for (final ProjectProperty projectProperty : projectProperties) {
			//SEEDING_DATE
			if (projectProperty.getTypeId().equals(TermId.SEEDING_DATE.getId()) && StringUtils.isNotBlank(projectProperty.getValue())) {
				final String plantingDate = projectProperty.getValue();
				samplesDetailsDto.setSeedingDate(plantingDate);
			}
			//CROP SEASON
			if (projectProperty.getTypeId().equals(TermId.SEASON_VAR_TEXT.getId()) && StringUtils.isNotBlank(projectProperty.getValue())) {
				final String season = projectProperty.getValue();
				samplesDetailsDto.setSeason(season);
			}
		}
	}

	private void fillPlotNoByExperimentProperty(final List<ExperimentProperty> experimentProperty,
		final SampleDetailsDTO sampleDetailsDTO) {
		boolean foundPlotNumber = false;
		Iterator<ExperimentProperty> experimentPropertyIterator = experimentProperty.iterator();
		while (experimentPropertyIterator.hasNext() && !foundPlotNumber) {
			ExperimentProperty properties = experimentPropertyIterator.next();
			if (properties.getTypeId().equals(TermId.PLOT_NO.getId())) {
				final Integer plotNumber = Integer.valueOf(properties.getValue());
				sampleDetailsDTO.setPlotNo(plotNumber);
				foundPlotNumber = true;
			}
		}
	}

	@Override
	public List<SampleDetailsDTO> getSamples(final Integer sampleListId) {
		final List<Sample> samples = this.sampleDao.getBySampleListId(sampleListId);
		final List<SampleDetailsDTO> result = new ArrayList<>();
		for (Sample sample : samples) {
			result.add(this.getSampleDetailsDTO(sample));
		}

		return result;
	}
}
