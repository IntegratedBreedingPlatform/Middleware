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

package org.generationcp.middleware.pojos;

import java.io.Serializable;

public class NumericData implements Serializable{

    private static final long serialVersionUID = 1L;

/*    public static final String GET_BY_OUNIT_ID_LIST = 
            "SELECT dn.ounitid, dn.variatid, v.vname, dn.dvalue " +
            "FROM data_n dn JOIN variate v ON dn.variatid = v.variatid " + 
            "WHERE dn.ounitid IN (:ounitIdList)";
*/

    protected NumericDataPK id;

    private Double value;

    private Variate variate;

    public NumericData() {
    }

    public NumericData(NumericDataPK id, Double value) {
        super();
        this.id = id;
        this.value = value;
    }

    public NumericDataPK getId() {
        return id;
    }

    public void setId(NumericDataPK id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Variate getVariate() {
        return variate;
    }

    public void setVariate(Variate variate) {
        this.variate = variate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
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
        NumericData other = (NumericData) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NumericData [id=");
        builder.append(id);
        builder.append(", value=");
        builder.append(value);
        builder.append(", variate=");
        builder.append(variate);
        builder.append("]");
        return builder.toString();
    }

}
