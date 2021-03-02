
package org.generationcp.middleware.domain.samplelist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.domain.sample.SampleDTO;

public class SampleListDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -3724999931592177161L;

	private Integer listId;
	private String listName;
	private String description;
	private String notes;
	private String createdBy;
	private String takenBy;
	private Date samplingDate;
	private String cropName;
	private Integer parentId;
	private Date createdDate;
	private String programUUID;

	// Creation from study
	private Integer datasetId;
	private Integer selectionVariableId;
	private List<Integer> instanceIds;

	// List builder
	private List<SampleDTO> entries;

	public String getCropName() {
		return this.cropName;
	}

	public void setCropName(final String cropName) {
		this.cropName = cropName;
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

	public Integer getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(final Integer datasetId) {
		this.datasetId = datasetId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(final Integer parentId) {
		this.parentId = parentId;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(final String listName) {
		this.listName = listName;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getProgramUUID() {
		return programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public List<SampleDTO> getEntries() {
		if (this.entries == null) {
			this.entries = new ArrayList<>();
		}
		return this.entries;
	}

	public void setEntries(final List<SampleDTO> entries) {
		this.entries = entries;
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
