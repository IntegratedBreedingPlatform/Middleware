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
@Table(name = "cvterm_alias",
		uniqueConstraints = {@UniqueConstraint(columnNames = {"cvterm_id", "program_id"})})
public class CVTermAlias implements Serializable {

	private static final long serialVersionUID = -6496723408899540369L;
    public static final String ID_NAME = "cvTermAliasId";

	@Id
	@Basic(optional = false)
	@Column(name = "cvterm_alias_id")
	private Integer cvTermAliasId;

	/**
	 * CvTermId
	 */
	@Column(name="cvterm_id")
	private Integer cvTermId;

	/**
	 * ProgramId
	 */
	@Column(name="program_id")
	private Integer programId;

	/**
	 * Alias name
	 */
	@Column(name = "name")
	private String name;


    public CVTermAlias() {
    }

    public CVTermAlias(Integer cvTermAliasId, Integer cvTermId, Integer programId, String name) {
        super();
        this.cvTermAliasId = cvTermAliasId;
		this.cvTermId = cvTermId;
		this.programId = programId;
		this.name = name;
    }
	public Integer getCvTermAliasId() {
		return cvTermAliasId;
	}

	public void setCvTermAliasId(Integer cvTermAliasId) {
		this.cvTermAliasId = cvTermAliasId;
	}

	public Integer getCvTermId() {
		return cvTermId;
	}

	public void setCvTermId(Integer cvTermId) {
		this.cvTermId = cvTermId;
	}

	public Integer getProgramId() {
		return programId;
	}

	public void setProgramId(Integer programId) {
		this.programId = programId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((cvTermAliasId == null) ? 0 : cvTermAliasId.hashCode());
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
		CVTermAlias other = (CVTermAlias) obj;
		if (cvTermAliasId == null) {
			if (other.cvTermAliasId != null) {
                return false;
            }
		} else if (!cvTermAliasId.equals(other.cvTermAliasId)) {
            return false;
        }
		return true;
	}

	@Override
	public String toString() {
		return "CVTermAlias{" +
				"cvTermAliasId=" + cvTermAliasId +
				", cvTermId=" + cvTermId +
				", programId=" + programId +
				", name='" + name + '\'' +
				'}';
	}
}
