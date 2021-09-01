package org.generationcp.middleware.api.nametype;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class NameTypeMetadataFilterRequest {

	private String code;
	private String name;
	private String description;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date nameTypeDateFrom;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date nameTypeDateTo;

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

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Date getNameTypeDateFrom() {
		return this.nameTypeDateFrom;
	}

	public void setNameTypeDateFrom(final Date nameTypeDateFrom) {
		this.nameTypeDateFrom = nameTypeDateFrom;
	}

	public Date getNameTypeDateTo() {
		return this.nameTypeDateTo;
	}

	public void setNameTypeDateTo(final Date nameTypeDateTo) {
		this.nameTypeDateTo = nameTypeDateTo;
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
