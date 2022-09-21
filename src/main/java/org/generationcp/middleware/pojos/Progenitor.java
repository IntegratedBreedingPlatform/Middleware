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

package org.generationcp.middleware.pojos;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * POJO for progntrs table.
 *
 * @author klmanansala
 */
@Entity
@Table(name = "progntrs")
public class Progenitor extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "gid", nullable = false)
	private Germplasm germplasm;
	
	@Basic(optional = false)
	@Column(name = "pno")
	private Integer progenitorNumber;

	@Basic(optional = false)
	@Column(name = "pid")
	private Integer progenitorGid;

	@OneToOne(fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "pid", insertable = false, updatable = false)
	private Germplasm progenitorGermplasm;

	public Progenitor() {
	}

	public Progenitor(final Germplasm germplasm, final Integer progenitorNumber, final Integer progenitorGid) {
		this.germplasm = germplasm;
		this.progenitorNumber = progenitorNumber;
		this.progenitorGid = progenitorGid;
	}

	public Integer getId() {
		return id;
	}

	
	public void setId(Integer id) {
		this.id = id;
	}

	
	
	public Germplasm getGermplasm() {
		return germplasm;
	}


	
	public void setGermplasm(Germplasm germplasm) {
		this.germplasm = germplasm;
	}


	public Integer getProgenitorNumber() {
		return progenitorNumber;
	}

	
	public void setProgenitorNumber(Integer progenitorNumber) {
		this.progenitorNumber = progenitorNumber;
	}

	public Integer getProgenitorGid() {
		return this.progenitorGid;
	}
	
	public void setProgenitorGid(Integer progenitorGid) {
		this.progenitorGid = progenitorGid;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Progenitor other = (Progenitor) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Progenitor [id=" + id + ", gid=" + germplasm.getGid() + ", progenitorNumber=" + progenitorNumber + ", progenitorGid="
				+ progenitorGid + "]";
	}

	public Germplasm getProgenitorGermplasm() {
		return this.progenitorGermplasm;
	}

	public void setProgenitorGermplasm(final Germplasm progenitorGermplasm) {
		this.progenitorGermplasm = progenitorGermplasm;
	}


}
