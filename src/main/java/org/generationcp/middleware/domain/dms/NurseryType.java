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
package org.generationcp.middleware.domain.dms;

/**
 * The different nursery types available 
 *
 */
public class NurseryType {

//	F1(702, "F1", "F1 Nursery"),
//	F2(703, "F2", "F2 Nursery"),
//	PN(704, "PN", "Pedigree Nursery");
    
	//udflds.fldno
	private int id;
	
    //udflds.fname
    private String name;
    
    //udflds.fcode
    private String code;
    
	//udflds.fdesc
	private String description;

	public NurseryType(int id, String name, String code, String description) {
		this.id = id;
		this.name = name;
		this.code = code;
		this.description = description;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
    
	public String getDescription() {
        return description;
    }

    
    public void setId(int id) {
        this.id = id;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        NurseryType other = (NurseryType) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NurseryType [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", code=");
        builder.append(code);
        builder.append(", description=");
        builder.append(description);
        builder.append("]");
        return builder.toString();
    }
	
	

}
