package org.generationcp.middleware.domain.search_request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.pojomatic.annotations.AutoProperty;

//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
//@JsonSubTypes(value = {@JsonSubTypes.Type(value = GermplasmSearchRequestDto.class, name = "GermplasmSearchRequestDto")})
public class SearchRequestDto {

}
