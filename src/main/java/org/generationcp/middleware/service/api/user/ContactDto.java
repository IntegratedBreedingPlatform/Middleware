package org.generationcp.middleware.service.api.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.StringUtils;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDto {

	private String contactDbId = StringUtils.EMPTY;

	private String name;

	private String email;

	private String type;

	private String orcid = StringUtils.EMPTY;

	private String instituteName = StringUtils.EMPTY;

	public ContactDto() {
	}

	public ContactDto(final String contactDbId, final String name, final String email, final String type, final String orcid,
		final String instituteName) {
		this.contactDbId = contactDbId;
		this.name = name;
		this.email = email;
		this.type = type;
		this.orcid = orcid;
		this.instituteName = instituteName;
	}

	public ContactDto(final String contactDbId, final String name, final String email, final String type) {
		this.contactDbId = contactDbId;
		this.name = name;
		this.email = email;
		this.type = type;
	}

	public String getContactDbId() {
		return this.contactDbId;
	}

	public void setContactDbId(final String contactDbId) {
		this.contactDbId = contactDbId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getOrcid() {
		return this.orcid;
	}

	public void setOrcid(final String orcid) {
		this.orcid = orcid;
	}

	public String getInstituteName() {
		return this.instituteName;
	}

	public void setInstituteName(final String instituteName) {
		this.instituteName = instituteName;
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	public boolean atLeastOneContactDetailProvided() {
		return StringUtils.isNotEmpty(this.getName()) || StringUtils.isNotEmpty(this.getEmail()) || StringUtils.isNotEmpty(this.getInstituteName()) || StringUtils.isNotEmpty(this.getType());
	}

	public void setFieldFromVariable(final Integer variableId, final String value) {
		if (ContactVariable.CONTACT_NAME.getId().equals(variableId)) {
			this.setName(value);
		} else if (ContactVariable.CONTACT_EMAIL.getId().equals(variableId)) {
			this.setEmail(value);
		} else if (ContactVariable.CONTACT_ORG.getId().equals(variableId)) {
			this.setInstituteName(value);
		} else if (ContactVariable.CONTACT_TYPE.getId().equals(variableId)) {
			this.setType(value);
		}
	}
}
