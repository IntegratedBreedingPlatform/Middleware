/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.pojos.oms;

import javax.persistence.*;
import java.io.Serializable;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_cvtermsynonym
 * 
 * @author tippsgo
 *
 */
@Entity
@Table(name = "cvterm_program_prop",
		uniqueConstraints = {@UniqueConstraint(columnNames = {"cvterm_id", "type_id", "program_uuid", "value", "rank"})})
public class CVTermProgramProperty implements Serializable {

	private static final long serialVersionUID = -6496723408899540369L;
    public static final String ID_NAME = "cvTermProgramPropertyId";

	@Id
	@Basic(optional = false)
	@Column(name = "cvterm_program_prop_id")
	private Integer cvTermProgramPropertyId;

	@Column(name="cvterm_id")
	private Integer cvTermId;

	/**
	 * Type of property.
	 *               1. Alias       1111
	 *               2. MinValue    1113
	 *               3. MaxValue    1115
	 */
	@Column(name = "type_id")
	private Integer typeId;

	/**
	 * Program UUID of term.
	 */
	@Column(name = "program_uuid")
	private String programUuid;

	/**
	 * Value of the property.
	 */
	@Column(name = "value")
	private String value;

	/**
	 * Rank of the property.
	 *             Rank should be zero and only single entry should be there.
	 *             Rank is kept for its future usage where we have multiple property for same type.
	 */
	@Column(name = "rank")
	private Integer rank;

    public CVTermProgramProperty() {
    }

    public CVTermProgramProperty(Integer cvTermPropertyId, Integer cvTermId, Integer typeId, String programUuid, String value, Integer rank) {
        super();
        this.cvTermProgramPropertyId = cvTermPropertyId;
        this.cvTermId = cvTermId;
        this.typeId = typeId;
        this.programUuid = programUuid;
		this.value = value;
        this.rank = rank;
    }

	public Integer getCvTermProgramPropertyId() {
		return cvTermProgramPropertyId;
	}

	public void setCvTermProgramPropertyId(Integer cvTermProgramPropertyId) {
		this.cvTermProgramPropertyId = cvTermProgramPropertyId;
	}

	public Integer getCvTermId() {
		return cvTermId;
	}

	public void setCvTermId(Integer cvTermId) {
		this.cvTermId = cvTermId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getProgramUuid() {
		return programUuid;
	}

	public void setProgramUuid(String programUuid) {
		this.programUuid = programUuid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((cvTermProgramPropertyId == null) ? 0 : cvTermProgramPropertyId.hashCode());
		return result;
	}

    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
		if (obj == null) {
            return false;
        }
		if (getClass() != obj.getClass()) {
            return false;
        }
		CVTermProgramProperty other = (CVTermProgramProperty) obj;
		if (cvTermProgramPropertyId == null) {
			if (other.cvTermProgramPropertyId != null) {
                return false;
            }
		} else if (!cvTermProgramPropertyId.equals(other.cvTermProgramPropertyId)) {
            return false;
        }
		return true;
	}

	@Override
	public String toString() {
		return "CVTermProgramProperty{" +
				"cvTermProgramPropertyId=" + cvTermProgramPropertyId +
				", cvTermId=" + cvTermId +
				", typeId=" + typeId +
				", programUuid='" + programUuid + '\'' +
				", value='" + value + '\'' +
				", rank=" + rank +
				'}';
	}
}
