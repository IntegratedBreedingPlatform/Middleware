package org.generationcp.middleware.api.brapi.v2.list;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;
import java.util.Map;

@AutoProperty
public class GermplasmListImportRequestDTO {

	private Map<String, String> additionalInfo;
	private List<String> data;
	private String dateCreated;
	private String dateModified;
	private List<ExternalReferenceDTO> externalReferences;
	private String listDescription;
	private String listName;
	private String listOwnerName;
	private String listOwnerPersonDbId;
	private Integer listSize;
	private String listSource;
	private String listType;

	public Map<String, String> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public List<String> getData() {
		return data;
	}

	public void setData(final List<String> data) {
		this.data = data;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(final String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getDateModified() {
		return dateModified;
	}

	public void setDateModified(final String dateModified) {
		this.dateModified = dateModified;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public String getListDescription() {
		return listDescription;
	}

	public void setListDescription(final String listDescription) {
		this.listDescription = listDescription;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(final String listName) {
		this.listName = listName;
	}

	public String getListOwnerName() {
		return listOwnerName;
	}

	public void setListOwnerName(final String listOwnerName) {
		this.listOwnerName = listOwnerName;
	}

	public String getListOwnerPersonDbId() {
		return listOwnerPersonDbId;
	}

	public void setListOwnerPersonDbId(final String listOwnerPersonDbId) {
		this.listOwnerPersonDbId = listOwnerPersonDbId;
	}

	public Integer getListSize() {
		return listSize;
	}

	public void setListSize(final Integer listSize) {
		this.listSize = listSize;
	}

	public String getListSource() {
		return listSource;
	}

	public void setListSource(final String listSource) {
		this.listSource = listSource;
	}

	public String getListType() {
		return listType;
	}

	public void setListType(final String listType) {
		this.listType = listType;
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
