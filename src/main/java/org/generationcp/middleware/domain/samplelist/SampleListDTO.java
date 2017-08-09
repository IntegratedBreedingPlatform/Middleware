package org.generationcp.middleware.domain.samplelist;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SampleListDTO implements Serializable {

	private Integer listId;

	private String description;

	private String type;

	private String notes;

	private String createdBy;

	private Integer selectionVariableId;

	private List<Integer> instanceIds;

	private String takenBy;

	private Date samplingDate;

	private Integer studyId;

	private String cropName;

	public String getCropName() {
		return cropName;
	}

	public void setCropName(String cropName) {
		this.cropName = cropName;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public Integer getSelectionVariableId() {
		return selectionVariableId;
	}

	public void setSelectionVariableId(Integer selectionVariableId) {
		this.selectionVariableId = selectionVariableId;
	}

	public List<Integer> getInstanceIds() {
		return instanceIds;
	}

	public void setInstanceIds(List<Integer> instanceIds) {
		this.instanceIds = instanceIds;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getTakenBy() {
		return takenBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setTakenBy(String takenBy) {

		this.takenBy = takenBy;
	}

	public Date getSamplingDate() {
		return samplingDate;
	}

	public void setSamplingDate(Date samplingDate) {
		this.samplingDate = samplingDate;
	}

	public Integer getListId() {
		return listId;
	}

	public void setListId(Integer listId) {
		this.listId = listId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof SampleListDTO)) {
			return false;
		}
		final SampleListDTO castOther = (SampleListDTO) other;
		return new EqualsBuilder().append(this.listId, castOther.listId).isEquals();
	}

	@Override
	public int hashCode() {

		return new HashCodeBuilder().append(this.listId).hashCode();
	}

	@Override
	public String toString() {

		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
