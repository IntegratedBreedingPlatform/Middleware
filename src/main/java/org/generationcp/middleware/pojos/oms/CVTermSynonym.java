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

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_cvtermsynonym
 *
 * A cvterm actually represents a distinct class or concept. A concept can be referred to by different phrases or names. In addition to the
 * primary name (cvterm.name) there can be a number of alternative aliases or synonyms. For example, "T cell" as a synonym for
 * "T lymphocyte".
 *
 * @author Darla Ani
 *
 */
@Entity
@Table(name = "cvtermsynonym")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="cvtermsynonym")
public class CVTermSynonym implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "cvtermsynonym_id")
	private Integer cvTermSynonymId;

	/**
	 * Alias or synonym for related CV Term
	 */
	@Basic(optional = false)
	@Column(name = "synonym")
	private String synonym;

	/**
	 * Related CVTerm type. A synonym can be exact, narrower, or broader than.
	 */
	@Column(name = "type_id")
	private Integer typeId;

	@Column(name = "cvterm_id")
	private Integer cvTermId;

	public CVTermSynonym() {

	}

	public CVTermSynonym(Integer id) {
		this.cvTermSynonymId = id;
	}

	public Integer getCvTermSynonymId() {
		return this.cvTermSynonymId;
	}

	public void setCvTermSynonymId(Integer id) {
		this.cvTermSynonymId = id;
	}

	public String getSynonym() {
		return this.synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getCvTermId() {
		return this.cvTermId;
	}

	public void setCvTermId(Integer cvTermId) {
		this.cvTermId = cvTermId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.cvTermSynonymId == null ? 0 : this.cvTermSynonymId.hashCode());
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
		if (!(obj instanceof CVTermSynonym)) {
			return false;
		}

		CVTermSynonym other = (CVTermSynonym) obj;
		if (this.cvTermSynonymId == null) {
			if (other.cvTermSynonymId != null) {
				return false;
			}
		} else if (!this.cvTermSynonymId.equals(other.cvTermSynonymId)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CVTermSynonym [cvTermSynonymId=");
		builder.append(this.cvTermSynonymId);
		builder.append(", synonym=");
		builder.append(this.synonym);
		builder.append(", typeId=");
		builder.append(this.typeId);
		builder.append("]");
		return builder.toString();
	}
}
