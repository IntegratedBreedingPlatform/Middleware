package org.generationcp.middleware.service.impl.audit;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmReferenceAuditDTO extends AbstractAuditDTO {

	private String value;

	private boolean valueChanged;

	public GermplasmReferenceAuditDTO() {
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public boolean isValueChanged() {
		return valueChanged;
	}

	public void setValueChanged(final boolean valueChanged) {
		this.valueChanged = valueChanged;
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
