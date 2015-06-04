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
import javax.persistence.Embeddable;

/**
 * The primary identifier of {@link Progenitor}.
 *
 * @author klmanansala
 */
@Embeddable
public class ProgenitorPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
	@Column(name = "gid")
	private Integer gid;

	@Basic(optional = false)
	@Column(name = "pno")
	private Integer pno;

	public ProgenitorPK() {
	}

	public ProgenitorPK(Integer gid, Integer pno) {
		this.gid = gid;
		this.pno = pno;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public Integer getPno() {
		return this.pno;
	}

	public void setPno(Integer pno) {
		this.pno = pno;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += this.gid;
		hash += this.pno;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof ProgenitorPK) {
			ProgenitorPK param = (ProgenitorPK) obj;
			if (this.getGid().equals(param.getGid()) && this.getPno().equals(param.getPno())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProgenitorPK [gid=");
		builder.append(this.gid);
		builder.append(", pno=");
		builder.append(this.pno);
		builder.append("]");
		return builder.toString();
	}

}
