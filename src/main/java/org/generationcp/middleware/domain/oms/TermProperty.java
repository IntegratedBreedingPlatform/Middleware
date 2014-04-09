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
package org.generationcp.middleware.domain.oms;

import java.io.Serializable;

/** 
 * Contains the properties of a Term - id, vocabularyId, name, definition, nameSynonyms, obsolete.
 */
public class TermProperty implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer termPropertyId;

	private Integer typeId;

	private String value;
	
	private Integer rank;

	public TermProperty(Integer termPropertyId, Integer typeId, String value,
            Integer rank) {
        super();
        this.termPropertyId = termPropertyId;
        this.typeId = typeId;
        this.value = value;
        this.rank = rank;
    }

    public Integer getTermPropertyId() {
        return termPropertyId;
    }
    
    public void setTermPropertyId(Integer termPropertyId) {
        this.termPropertyId = termPropertyId;
    }
    
    public Integer getTypeId() {
        return typeId;
    }
    
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
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
        result = prime * result + ((rank == null) ? 0 : rank.hashCode());
        result = prime * result
                + ((termPropertyId == null) ? 0 : termPropertyId.hashCode());
        result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        TermProperty other = (TermProperty) obj;
        if (rank == null) {
            if (other.rank != null)
                return false;
        } else if (!rank.equals(other.rank))
            return false;
        if (termPropertyId == null) {
            if (other.termPropertyId != null)
                return false;
        } else if (!termPropertyId.equals(other.termPropertyId))
            return false;
        if (typeId == null) {
            if (other.typeId != null)
                return false;
        } else if (!typeId.equals(other.typeId))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TermProperty [termPropertyId=");
        builder.append(termPropertyId);
        builder.append(", typeId=");
        builder.append(typeId);
        builder.append(", value=");
        builder.append(value);
        builder.append(", rank=");
        builder.append(rank);
        builder.append("]");
        return builder.toString();
    }
	


}
