package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.service.impl.audit.RevisionType;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class ObservationAuditDTO {

	private Integer phenotypeId;
	private String value;
	private Integer variableId;
	private String draftValue;
	private String updatedBy;
	private Integer updatedByUserId;
	private Date updatedDate;
	private RevisionType revisionType;

	private boolean valueChanged;
	private boolean draftValueChanged;

	public ObservationAuditDTO() {
	}

	public ObservationAuditDTO(final String value) {
		this.value = value;
	}

	public Integer getPhenotypeId() {
		return this.phenotypeId;
	}

	public void setPhenotypeId(final Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}

	public boolean isValueChanged() {
		return this.valueChanged;
	}

	public void setValueChanged(final boolean valueChanged) {
		this.valueChanged = valueChanged;
	}

	public boolean isDraftValueChanged() {
		return this.draftValueChanged;
	}

	public void setDraftValueChanged(final boolean draftValueChanged) {
		this.draftValueChanged = draftValueChanged;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getVariableId() {
		return this.variableId;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
	}

	public String getDraftValue() {
		return this.draftValue;
	}

	public void setDraftValue(final String draftValue) {
		this.draftValue = draftValue;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(final String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(final Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public RevisionType getRevisionType() {
		return this.revisionType;
	}

	public void setRevisionType(final RevisionType revisionType) {
		this.revisionType = revisionType;
	}

	public Integer getUpdatedByUserId() {
		return this.updatedByUserId;
	}

	public void setUpdatedByUserId(final Integer updatedByUserId) {
		this.updatedByUserId = updatedByUserId;
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
