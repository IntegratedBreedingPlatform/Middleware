package org.generationcp.middleware.domain.samplelist;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.service.api.user.UserDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SampleListDTO implements Serializable {

  private Integer listId;

  private String listName;

  private String description;

  private SampleListDTO hierarchy;

  private String type;

  private Date createdDate;

  private String notes;

  private UserDto createdBy;

  private List<SampleDTO> samples;

  public List<SampleDTO> getSamples() {
	return samples;
  }

  public void setSamples(List<SampleDTO> samples) {
	this.samples = samples;
  }

  public Integer getListId() {
	return listId;
  }

  public void setListId(Integer listId) {
	this.listId = listId;
  }

  public String getListName() {
	return listName;
  }

  public void setListName(String listName) {
	this.listName = listName;
  }

  public String getDescription() {
	return description;
  }

  public void setDescription(String description) {
	this.description = description;
  }

  public SampleListDTO getHierarchy() {
	return hierarchy;
  }

  public void setHierarchy(SampleListDTO hierarchy) {
	this.hierarchy = hierarchy;
  }

  public String getType() {
	return type;
  }

  public void setType(String type) {
	this.type = type;
  }

  public Date getCreatedDate() {
	return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
	this.createdDate = createdDate;
  }

  public String getNotes() {
	return notes;
  }

  public void setNotes(String notes) {
	this.notes = notes;
  }

  public UserDto getCreatedBy() {
	return createdBy;
  }

  public void setCreatedBy(UserDto createdBy) {
	this.createdBy = createdBy;
  }

  @Override public boolean equals(final Object other) {
	if (!(other instanceof SampleListDTO)) {
	  return false;
	}
	final SampleListDTO castOther = (SampleListDTO) other;
	return new EqualsBuilder().append(this.listId, castOther.listId).isEquals();
  }

  @Override public int hashCode() {

	return new HashCodeBuilder().append(this.listId).hashCode();
  }

  @Override public String toString() {

	return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
  }
}
