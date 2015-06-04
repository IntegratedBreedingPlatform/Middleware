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

package org.generationcp.middleware.domain.oms;

import java.io.Serializable;

/**
 * Contains the properties of a Term - id, vocabularyId, name, definition, nameSynonyms, obsolete.
 */
public class TermProperty implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer termPropertyId;

	private Integer typeId;

	private String value;

	private Integer rank;

	public TermProperty(Integer termPropertyId, Integer typeId, String value, Integer rank) {
		super();
		this.termPropertyId = termPropertyId;
		this.typeId = typeId;
		this.value = value;
		this.rank = rank;
	}

	public Integer getTermPropertyId() {
		return this.termPropertyId;
	}

	public void setTermPropertyId(Integer termPropertyId) {
		this.termPropertyId = termPropertyId;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getRank() {
		return this.rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.rank == null ? 0 : this.rank.hashCode());
		result = prime * result + (this.termPropertyId == null ? 0 : this.termPropertyId.hashCode());
		result = prime * result + (this.typeId == null ? 0 : this.typeId.hashCode());
		result = prime * result + (this.value == null ? 0 : this.value.hashCode());
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
		TermProperty other = (TermProperty) obj;
		if (this.rank == null) {
			if (other.rank != null) {
				return false;
			}
		} else if (!this.rank.equals(other.rank)) {
			return false;
		}
		if (this.termPropertyId == null) {
			if (other.termPropertyId != null) {
				return false;
			}
		} else if (!this.termPropertyId.equals(other.termPropertyId)) {
			return false;
		}
		if (this.typeId == null) {
			if (other.typeId != null) {
				return false;
			}
		} else if (!this.typeId.equals(other.typeId)) {
			return false;
		}
		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TermProperty [termPropertyId=");
		builder.append(this.termPropertyId);
		builder.append(", typeId=");
		builder.append(this.typeId);
		builder.append(", value=");
		builder.append(this.value);
		builder.append(", rank=");
		builder.append(this.rank);
		builder.append("]");
		return builder.toString();
	}

}
