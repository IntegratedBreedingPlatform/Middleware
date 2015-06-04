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
@Table(name = "variable_program_overrides", uniqueConstraints = {@UniqueConstraint(columnNames = {"cvterm_id", "program_uuid"})})
public class VariableProgramOverrides implements Serializable {

	private static final long serialVersionUID = -6496723408899540369L;
	public static final String ID_NAME = "id";

	@Id
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
	@Column(name = "min_value")
	private String minValue;

	/**
	 * Variable alias
	 */
	@Column(name = "max_value")
	private String maxValue;

	public VariableProgramOverrides() {

	}

	public VariableProgramOverrides(Integer id, Integer variableId, String programUuid, String alias, String minValue, String maxValue) {
		super();

		this.id = id;
		this.variableId = variableId;
		this.programUuid = programUuid;
		this.alias = alias;
		this.minValue = minValue;
		this.maxValue = maxValue;
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

	public String getMinValue() {
		return this.minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return this.maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
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
		VariableProgramOverrides other = (VariableProgramOverrides) obj;
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
		return "VariableProgramOverrides{" + "id=" + this.id + ", variableId=" + this.variableId + ", programUuid='" + this.programUuid
				+ '\'' + ", alias='" + this.alias + '\'' + ", minValue='" + this.minValue + '\'' + ", maxValue='" + this.maxValue + '\''
				+ '}';
	}
}
