package org.generationcp.middleware.api.brapi.v2.attribute;

import org.generationcp.middleware.domain.search_request.brapi.v2.AttributeValueSearchRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AttributeValueServiceBrapi {

	List<AttributeValueDto> getAttributeValues(AttributeValueSearchRequestDto attributeValueSearchRequestDto, Pageable pageable,
		final String programUUID);

	long countAttributeValues(AttributeValueSearchRequestDto attributeValueSearchRequestDto, String programUUID);
}
