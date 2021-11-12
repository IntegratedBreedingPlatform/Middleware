package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.search_request.brapi.v2.SampleSearchRequestDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.service.api.sample.SampleObservationDto;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Repository
@Transactional
public class SampleServiceImpl implements SampleService {

	private static final String SAMPLE_KEY_PREFIX = "S";

	private final HibernateSessionProvider sessionProvider;

	@Autowired
	private UserService userService;

	private final DaoFactory daoFactory;

	public SampleServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
	}

	@Override
	public Sample buildSample(final String cropName, final String cropPrefix, final Integer entryNumber,
		final String sampleName, final Date samplingDate, final Integer experimentId, final SampleList sampleList, final Integer createdBy,
		final Date createdDate, final Integer takenBy, final Integer sampleNumber) {

		final Sample sample = new Sample();
		sample.setTakenBy(takenBy);
		sample.setEntryNumber(entryNumber);
		sample.setSampleName(sampleName);
		sample.setCreatedDate(new Date());
		sample.setSamplingDate(samplingDate);
		sample.setSampleBusinessKey(this.getSampleBusinessKey(cropPrefix));
		sample.setSampleList(sampleList);
		sample.setCreatedDate(createdDate);
		sample.setCreatedBy(createdBy);
		sample.setExperiment(this.daoFactory.getExperimentDao().getById(experimentId));
		sample.setSampleNumber(sampleNumber);

		return sample;
	}

	private String getSampleBusinessKey(final String cropPrefix) {
		String sampleBusinessKey = cropPrefix;
		sampleBusinessKey = sampleBusinessKey + SampleServiceImpl.SAMPLE_KEY_PREFIX;
		sampleBusinessKey = sampleBusinessKey + RandomStringUtils.randomAlphanumeric(8);

		return sampleBusinessKey;
	}

	@Override
	public List<SampleDTO> filter(final String obsUnitId, final Integer listId, final Pageable pageable) {
		Integer ndExperimentId = null;
		final Optional<ExperimentModel> experimentModelOptional = this.daoFactory.getExperimentDao().getByObsUnitId(obsUnitId);
		if (experimentModelOptional.isPresent()) {
			ndExperimentId = experimentModelOptional.get().getNdExperimentId();
		}
		final List<SampleDTO> sampleDTOS = this.daoFactory.getSampleDao().filter(ndExperimentId, listId, pageable);
		this.populateTakenBy(sampleDTOS);
		return sampleDTOS;
	}

	@Override
	public long countFilter(final String obsUnitId, final Integer listId) {

		Integer ndExperimentId = null;
		final Optional<ExperimentModel> experimentModelOptional = this.daoFactory.getExperimentDao().getByObsUnitId(obsUnitId);
		if (experimentModelOptional.isPresent()) {
			ndExperimentId = experimentModelOptional.get().getNdExperimentId();
		}
		return this.daoFactory.getSampleDao().countFilter(ndExperimentId, listId);
	}

	@Override
	public Map<String, SampleDTO> getSamplesBySampleUID(final Set<String> sampleUIDs) {
		final List<SampleDTO> sampleDTOs = this.daoFactory.getSampleDao().getBySampleBks(sampleUIDs);
		return Maps.uniqueIndex(sampleDTOs, new Function<SampleDTO, String>() {

			public String apply(final SampleDTO from) {
				return from.getSampleBusinessKey();
			}
		});
	}

	@Override
	public List<SampleDTO> getByGid(final Integer gid) {
		final List<SampleDTO> sampleDTOS = this.daoFactory.getSampleDao().getByGid(gid);
		this.populateTakenBy(sampleDTOS);
		return sampleDTOS;
	}

	@Override
	public Boolean studyHasSamples(final Integer studyId) {
		return this.daoFactory.getSampleDao().hasSamples(studyId);
	}

	@Override
	public Boolean studyEntryHasSamples(final Integer studyId, final Integer entryId) {
		return this.daoFactory.getSampleDao().studyEntryHasSamples(studyId, entryId);
	}

	@Override
	public List<SampleObservationDto> getSampleObservations(final SampleSearchRequestDTO requestDTO, final Pageable pageable) {
		final List<SampleObservationDto> sampleObservationDtos = this.daoFactory.getSampleDao().getSampleObservationDtos(requestDTO, pageable);
		if (!CollectionUtils.isEmpty(sampleObservationDtos)) {
			final List<Integer> sampleIds = new ArrayList<>(sampleObservationDtos.stream().map(SampleObservationDto::getSampleId)
					.collect(Collectors.toSet()));
			final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
					this.daoFactory.getSampleExternalReferenceDAO().getExternalReferences(sampleIds).stream()
							.collect(groupingBy(
									ExternalReferenceDTO::getEntityId));
			final List<Integer> userIds = sampleObservationDtos.stream().map(SampleObservationDto::getTakenById).collect(Collectors.toList());
			final Map<Integer, String> userIDFullNameMap = this.userService.getUserIDFullNameMap(userIds);
			for(final SampleObservationDto sample: sampleObservationDtos) {
				sample.setExternalReferences(externalReferencesMap.get(sample.getSampleId().toString()));
				sample.setTakenBy(userIDFullNameMap.get(sample.getTakenById()));
			}
		}
		return sampleObservationDtos;
	}

	@Override
	public long countSampleObservations(final SampleSearchRequestDTO sampleSearchRequestDTO) {
		return this.daoFactory.getSampleDao().countSampleObservationDtos(sampleSearchRequestDTO);
	}

	void populateTakenBy(final List<SampleDTO> sampleDTOS) {
		// Populate takenBy with full name of user from workbench database.
		final List<Integer> userIds = sampleDTOS.stream().map(SampleDTO::getTakenByUserId).collect(Collectors.toList());
		final Map<Integer, String> userIDFullNameMap = this.userService.getUserIDFullNameMap(userIds);
		sampleDTOS.forEach(sampleDTO -> sampleDTO.setTakenBy(userIDFullNameMap.get(sampleDTO.getTakenByUserId())));
	}

}
