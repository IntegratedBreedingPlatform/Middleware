package org.generationcp.middleware.api.brapi.v2.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.util.serializer.DatePropertySerializer;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AutoProperty
public class AttributeValueDto {

	@JsonIgnore
	private Integer aid;

	@JsonIgnore
	private String adate;

	@JsonIgnore
	private String locationDbId;

	private Map<String, String> additionalInfo;
	private String attributeValueDbId;
	private String attributeDbId;
	private String attributeName;

	@JsonSerialize(using = DatePropertySerializer.class)
	private Date determinedDate;

	private List<ExternalReferenceDTO> externalReferences;
	private String germplasmDbId;
	private String germplasmName;
	private String value;

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getAttributeValueDbId() {
		return this.attributeValueDbId;
	}

	public void setAttributeValueDbId(final String attributeValueDbId) {
		this.attributeValueDbId = attributeValueDbId;
	}

	public String getAttributeDbId() {
		return this.attributeDbId;
	}

	public void setAttributeDbId(final String attributeDbId) {
		this.attributeDbId = attributeDbId;
	}

	public String getAttributeName() {
		return this.attributeName;
	}

	public void setAttributeName(final String attributeName) {
		this.attributeName = attributeName;
	}

	public Date getDeterminedDate() {
		return this.determinedDate;
	}

	public void setDeterminedDate(final Date determinedDate) {
		this.determinedDate = determinedDate;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public String getGermplasmDbId() {
		return this.germplasmDbId;
	}

	public void setGermplasmDbId(final String germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public String getGermplasmName() {
		return this.germplasmName;
	}

	public void setGermplasmName(final String germplasmName) {
		this.germplasmName = germplasmName;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getAid() {
		return this.aid;
	}

	public void setAid(final Integer aid) {
		this.aid = aid;
	}

	public String getLocationDbId() {
		return this.locationDbId;
	}

	public void setLocationDbId(final String locationDbId) {
		this.locationDbId = locationDbId;
	}

	public String getAdate() {
		return this.adate;
	}

	public void setAdate(final String adate) {
		this.adate = adate;
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}
