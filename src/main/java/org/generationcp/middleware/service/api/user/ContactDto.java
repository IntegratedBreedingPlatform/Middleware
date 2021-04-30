package org.generationcp.middleware.service.api.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.StringUtils;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDto {

	private String contactDbId;

	private String name;

	private String email;

	private String type;

	private String orcid = StringUtils.EMPTY;

	private String instituteName = StringUtils.EMPTY;

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
		return orcid;
	}

	public void setOrcid(final String orcid) {
		this.orcid = orcid;
	}

	public String getInstituteName() {
		return instituteName;
	}

	public void setInstituteName(final String instituteName) {
		this.instituteName = instituteName;
	}
}
