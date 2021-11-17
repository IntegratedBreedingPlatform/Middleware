package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.domain.search_request.brapi.v2.SampleSearchRequestDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.sample.SampleObservationDto;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class SampleServiceBrapiImpl implements SampleServiceBrapi {

	private final HibernateSessionProvider sessionProvider;

	@Autowired
	private UserService userService;

	private final DaoFactory daoFactory;

	public SampleServiceBrapiImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
	}

	@Override
	public List<SampleObservationDto> getSampleObservations(final SampleSearchRequestDTO requestDTO, final Pageable pageable) {
		final List<SampleObservationDto> sampleObservationDtos =
			this.daoFactory.getSampleDao().getSampleObservationDtos(requestDTO, pageable);
		if (!CollectionUtils.isEmpty(sampleObservationDtos)) {
			final List<Integer> sampleIds = new ArrayList<>(sampleObservationDtos.stream().map(SampleObservationDto::getSampleId)
				.collect(Collectors.toSet()));
			final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
				this.daoFactory.getSampleExternalReferenceDAO().getExternalReferences(sampleIds).stream()
					.collect(groupingBy(
						ExternalReferenceDTO::getEntityId));
			final List<Integer> userIds =
				sampleObservationDtos.stream().map(SampleObservationDto::getTakenById).collect(Collectors.toList());
			final Map<Integer, String> userIDFullNameMap = this.userService.getUserIDFullNameMap(userIds);
			for (final SampleObservationDto sample : sampleObservationDtos) {
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
}
