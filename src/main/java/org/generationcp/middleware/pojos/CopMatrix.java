package org.generationcp.middleware.pojos;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Coefficient of parentage matrix
 */
@Entity
@Table(name = "cop_matrix")
public class CopMatrix implements Serializable {

	@Id
	@Basic(optional = false)
	@Column(name = "gid1")
	private Integer gid1;

	@Id
	@Basic(optional = false)
	@Column(name = "gid2")
	private Integer gid2;

	@Basic(optional = false)
	@Column(name = "cop")
	private Double cop;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gid1", insertable = false, updatable = false)
	private Germplasm germplasm1;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gid2", insertable = false, updatable = false)
	private Germplasm germplasm2;

	public CopMatrix() {
	}

	public CopMatrix(final Integer gid1, final Integer gid2, final Double cop) {
		this.gid1 = gid1;
		this.gid2 = gid2;
		this.cop = cop;
	}

	public Integer getGid1() {
		return gid1;
	}

	public void setGid1(final Integer gid1) {
		this.gid1 = gid1;
	}

	public Integer getGid2() {
		return gid2;
	}

	public void setGid2(final Integer gid2) {
		this.gid2 = gid2;
	}

	public Double getCop() {
		return cop;
	}

	public void setCop(final Double cop) {
		this.cop = cop;
	}

	public Germplasm getGermplasm1() {
		return germplasm1;
	}

	public void setGermplasm1(final Germplasm germplasm1) {
		this.germplasm1 = germplasm1;
	}

	public Germplasm getGermplasm2() {
		return germplasm2;
	}

	public void setGermplasm2(final Germplasm germplasm2) {
		this.germplasm2 = germplasm2;
	}
}
