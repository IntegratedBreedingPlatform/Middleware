package org.generationcp.middleware.api.breedingmethod;

import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class MethodClassDTO {

	private Integer id;
	private String name;
	private String description;
	private String methodTypeCode;

	public MethodClassDTO() {
	}

	public MethodClassDTO(final CVTerm cvTerm, final MethodType methodType) {
		this();
		this.id = cvTerm.getCvTermId();
		this.name = cvTerm.getName();
		this.description = cvTerm.getDefinition();
		this.methodTypeCode = methodType.getCode();
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
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

	public String getMethodTypeCode() {
		return this.methodTypeCode;
	}

	public void setMethodTypeCode(final String methodTypeCode) {
		this.methodTypeCode = methodTypeCode;
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
