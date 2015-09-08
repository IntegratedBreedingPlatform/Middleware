/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.oms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_variableprogramoverrides
 *
 * @author tippsgo
 *
 */
@Entity
@Table(name = "variable_overrides", uniqueConstraints = {@UniqueConstraint(columnNames = {"cvterm_id", "program_uuid"})})
public class VariableOverrides implements Serializable {

	private static final long serialVersionUID = -6496723408899540369L;
	public static final String ID_NAME = "id";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	/**
	 * Variable Id
	 */
	@Column(name = "cvterm_id")
	private Integer variableId;

	/**
	 * Program UUID of term.
	 */
	@Column(name = "program_uuid")
	private String programUuid;

	/**
	 * Variable alias
	 */
	@Column(name = "alias")
	private String alias;

	/**
	 * Variable alias
	 */
	@Column(name = "expected_min")
	private String expectedMin;

	/**
	 * Variable alias
	 */
	@Column(name = "expected_max")
	private String expectedMax;

	public VariableOverrides() {

	}

	public VariableOverrides(Integer id, Integer variableId, String programUuid, String alias, String expectedMin, String expectedMax) {
		super();

		this.id = id;
		this.variableId = variableId;
		this.programUuid = programUuid;
		this.alias = alias;
		this.expectedMin = expectedMin;
		this.expectedMax = expectedMax;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getVariableId() {
		return this.variableId;
	}

	public void setVariableId(Integer variableId) {
		this.variableId = variableId;
	}

	public String getProgramUuid() {
		return this.programUuid;
	}

	public void setProgramUuid(String programUuid) {
		this.programUuid = programUuid;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getExpectedMin() {
		return this.expectedMin;
	}

	public void setExpectedMin(String minValue) {
		this.expectedMin = minValue;
	}

	public String getExpectedMax() {
		return this.expectedMax;
	}

	public void setExpectedMax(String maxValue) {
		this.expectedMax = maxValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.id == null ? 0 : this.id.hashCode());
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
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		VariableOverrides other = (VariableOverrides) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "VariableOverrides{" + "id=" + this.id + ", variableId=" + this.variableId + ", programUuid='" + this.programUuid + '\''
				+ ", alias='" + this.alias + '\'' + ", minValue='" + this.expectedMin + '\'' + ", maxValue='" + this.expectedMax + '\''
				+ '}';
	}
}
