package org.generationcp.middleware.service.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoProperty
public class GermplasmListDTO implements Serializable {

	private Map<String, String> additionalInfo = new HashMap<>();

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date dateCreated;

	private String dateModified = StringUtils.EMPTY;

	private List<ExternalReferenceDTO> externalReferences;

	private String listDbId;

	private String listDescription;

	private String listName;

	private String listOwnerName;

	private String listOwnerPersonDbId;

	private Integer listSize;

	private String listSource = StringUtils.EMPTY;

	private String listType = "germplasm";

	public Map<String, String> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(final Date dateCreated) {
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

	public String getListDbId() {
		return listDbId;
	}

	public void setListDbId(final String listDbId) {
		this.listDbId = listDbId;
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
