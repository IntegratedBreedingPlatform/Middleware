
package org.generationcp.middleware.domain.samplelist;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SampleListDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -3724999931592177161L;

	private Integer listId;

	private String description;

	private String notes;

	private String createdBy;

	private Integer selectionVariableId;

	private List<Integer> instanceIds;

	private String takenBy;

	private Date samplingDate;

	private Integer studyId;

	private String cropName;

	private Integer parentId;

	private String listName;

	private Date createdDate;

	private String programUUID;

	public String getCropName() {
		return this.cropName;
	}

	public void setCropName(final String cropName) {
		this.cropName = cropName;
	}

	public Integer getStudyId() {
		return this.studyId;
	}

	public void setStudyId(final Integer studyId) {
		this.studyId = studyId;
	}

	public Integer getSelectionVariableId() {
		return this.selectionVariableId;
	}

	public void setSelectionVariableId(final Integer selectionVariableId) {
		this.selectionVariableId = selectionVariableId;
	}

	public List<Integer> getInstanceIds() {
		return this.instanceIds;
	}

	public void setInstanceIds(final List<Integer> instanceIds) {
		this.instanceIds = instanceIds;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public String getTakenBy() {
		return this.takenBy;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setTakenBy(final String takenBy) {

		this.takenBy = takenBy;
	}

	public Date getSamplingDate() {
		return this.samplingDate;
	}

	public void setSamplingDate(final Date samplingDate) {
		this.samplingDate = samplingDate;
	}

	public Integer getListId() {
		return this.listId;
	}

	public void setListId(final Integer listId) {
		this.listId = listId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(final String notes) {
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

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getProgramUUID() {
		return programUUID;
	}

	public void setProgramUUID(String programUUID) {
		this.programUUID = programUUID;
	}
}
