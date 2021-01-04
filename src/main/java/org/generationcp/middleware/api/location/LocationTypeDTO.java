package org.generationcp.middleware.api.location;

import org.generationcp.middleware.pojos.UserDefinedField;

public class LocationTypeDTO {

	private Integer id;
	private String code;
	private String name;

	public LocationTypeDTO() {
	}

	public LocationTypeDTO(final UserDefinedField userDefinedField) {
		this();
		this.id = userDefinedField.getFldno();
		this.code = userDefinedField.getFcode();
		this.name = userDefinedField.getFname();
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
