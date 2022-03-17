package org.generationcp.middleware.domain.search_request.brapi.v2;

import org.generationcp.middleware.domain.search_request.SearchRequestDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmListSearchRequestDTO extends SearchRequestDto {

	private String listType;
	private String listName;
	private String listDbId;
	private String listSource;
	private String externalReferenceID;
	private String externalReferenceSource;

	public GermplasmListSearchRequestDTO(final String listType, final String listName, final String listDbId, final String listSource,
		final String externalReferenceID, final String externalReferenceSource) {
		this.listType = listType;
		this.listName = listName;
		this.listDbId = listDbId;
		this.listSource = listSource;
		this.externalReferenceID = externalReferenceID;
		this.externalReferenceSource = externalReferenceSource;
	}

	public GermplasmListSearchRequestDTO() {
	}

	public String getListType() {
		return this.listType;
	}

	public void setListType(final String listType) {
		this.listType = listType;
	}

	public String getListName() {
		return this.listName;
	}

	public void setListName(final String listName) {
		this.listName = listName;
	}

	public String getListDbId() {
		return this.listDbId;
	}

	public void setListDbId(final String listDbId) {
		this.listDbId = listDbId;
	}

	public String getListSource() {
		return this.listSource;
	}

	public void setListSource(final String listSource) {
		this.listSource = listSource;
	}

	public String getExternalReferenceID() {
		return this.externalReferenceID;
	}

	public void setExternalReferenceID(final String externalReferenceID) {
		this.externalReferenceID = externalReferenceID;
	}

	public String getExternalReferenceSource() {
		return this.externalReferenceSource;
	}

	public void setExternalReferenceSource(final String externalReferenceSource) {
		this.externalReferenceSource = externalReferenceSource;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}
}
