package org.generationcp.middleware.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "program_location_default")
public class ProgramLocationDefault extends AbstractEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@Basic(optional = false)
	@Column(name = "program_uuid")
	private String programUUID;

	@Basic(optional = false)
	@Column(name = "breeding_location_id")
	private Integer breedingLocationId;

	@Basic(optional = false)
	@Column(name = "storage_location_id")
	private Integer storageLocationId;

	public ProgramLocationDefault() {
	}

	public ProgramLocationDefault(final String programUUID, final Integer locationId, final Integer storageLocationId) {
		this.programUUID = programUUID;
		this.breedingLocationId = locationId;
		this.storageLocationId = storageLocationId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getProgramUUID() {
		return this.programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public Integer getBreedingLocationId() {
		return this.breedingLocationId;
	}

	public void setBreedingLocationId(final Integer locationId) {
		this.breedingLocationId = locationId;
	}

	public Integer getStorageLocationId() {
		return this.storageLocationId;
	}

	public void setStorageLocationId(final Integer storageLocationId) {
		this.storageLocationId = storageLocationId;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ProgramLocationDefault)) {
			return false;
		}
		final ProgramLocationDefault castOther = (ProgramLocationDefault) other;
		return new EqualsBuilder().append(this.id, castOther.id).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).hashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

}
