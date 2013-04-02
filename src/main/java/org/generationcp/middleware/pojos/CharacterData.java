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

public class CharacterData implements Serializable{

    private static final long serialVersionUID = 1L;

/*    public static final String GET_BY_OUNIT_ID_LIST = 
            "SELECT dc.ounitid, dc.variatid, v.vname, dc.dvalue " +
            "FROM data_c dc JOIN variate v on dc.variatid = v.variatid " + 
            "WHERE dc.ounitid IN (:ounitIdList)";
*/

    protected CharacterDataPK id;

    private String value;

    private Variate variate;

    public CharacterData() {
    }

    public CharacterData(CharacterDataPK id, String value) {
        super();
        this.id = id;
        this.value = value;
    }

    public CharacterDataPK getId() {
        return id;
    }

    public void setId(CharacterDataPK id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
        CharacterData other = (CharacterData) obj;
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
        builder.append("CharacterData [id=");
        builder.append(id);
        builder.append(", value=");
        builder.append(value);
        builder.append(", variate=");
        builder.append(variate);
        builder.append("]");
        return builder.toString();
    }

}
