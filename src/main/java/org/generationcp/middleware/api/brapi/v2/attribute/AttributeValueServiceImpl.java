package org.generationcp.middleware.api.brapi.v2.attribute;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.domain.search_request.brapi.v2.AttributeValueSearchRequestDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
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
public class AttributeValueServiceImpl implements AttributeValueService {

	private final HibernateSessionProvider sessionProvider;

	@Autowired
	private UserService userService;

	private final DaoFactory daoFactory;

	public AttributeValueServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
	}

	@Override
	public List<AttributeValueDto> getAttributeValues(final AttributeValueSearchRequestDto attributeValueSearchRequestDto,
		final Pageable pageable, final String programUUID) {
		final List<AttributeValueDto> dtos = this.daoFactory.getAttributeDAO().getAttributeValueDtos(
			attributeValueSearchRequestDto, pageable, programUUID);

		if (!CollectionUtils.isEmpty(dtos)) {
			final List<Integer> attributeIds = new ArrayList<>(dtos.stream().map(AttributeValueDto::getAid)
				.collect(Collectors.toSet()));
			final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
				this.daoFactory.getAttributeExternalReferenceDao().getExternalReferences(attributeIds).stream()
					.collect(groupingBy(
						ExternalReferenceDTO::getEntityId));
			for (final AttributeValueDto attributeValue : dtos) {
				attributeValue.setExternalReferences(externalReferencesMap.get(attributeValue.getAid().toString()));
			}
		}

		return dtos;
	}

	@Override
	public long countAttributeValues(final AttributeValueSearchRequestDto attributeValueSearchRequestDto,
		final String programUUID) {
		return this.daoFactory.getAttributeDAO().countAttributeValueDtos(attributeValueSearchRequestDto, programUUID);
	}
}
