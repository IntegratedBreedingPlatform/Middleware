package org.generationcp.middleware.api.breedingmethod;

import org.generationcp.middleware.pojos.oms.CVTerm;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class MethodClassDTO {

	private Integer id;
	private String name;
	private final String description;

	public MethodClassDTO(final CVTerm cvTerm) {
		this.id = cvTerm.getCvTermId();
		this.name = cvTerm.getName();
		this.description = cvTerm.getDefinition();
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
