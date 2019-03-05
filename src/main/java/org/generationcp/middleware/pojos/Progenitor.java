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

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * POJO for progntrs table.
 *
 * @author klmanansala
 */
@Entity
@Table(name = "progntrs")
public class Progenitor implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne(targetEntity = Germplasm.class)
	@JoinColumn(name = "gid", nullable = true)
	private Germplasm germplasm;
	
	@Basic(optional = false)
	@Column(name = "pno")
	private Integer progenitorNumber;

	@Basic(optional = false)
	@Column(name = "pid")
	private Integer progenitorGid;
	
	
	public Progenitor(Integer id) {
		super();
		this.id = id;
	}


	public Progenitor(final Germplasm germplasm, final Integer progenitorNumber, final Integer progenitorGid) {
		super();
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





}
