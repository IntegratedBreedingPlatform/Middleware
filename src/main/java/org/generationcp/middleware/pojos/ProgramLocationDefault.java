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
	@Column(name = "location_id")
	private Integer locationId;

	public ProgramLocationDefault() {
	}

	public ProgramLocationDefault(final String programUUID, final Integer locationId) {
		this.programUUID = programUUID;
		this.locationId = locationId;
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

	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
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
