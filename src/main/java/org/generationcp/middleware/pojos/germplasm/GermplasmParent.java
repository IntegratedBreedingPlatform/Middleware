package org.generationcp.middleware.pojos.germplasm;

import java.io.Serializable;

public class GermplasmParent implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected Integer gid;
	protected String designation;
	protected String pedigree;
	
	public GermplasmParent() {
		// Declare empty consructor for child class
	}
	
	public GermplasmParent(Integer gid, final String designation, String pedigree) {
		super();
		this.gid = gid;
		this.designation = designation;
		this.pedigree = pedigree;
	}

	public Integer getGid() {
		return gid;
	}
	
	public void setGid(Integer gid) {
		this.gid = gid;
	}
	
	public String getDesignation() {
		return designation;
	}
	
	public void setDesignations(String name) {
		this.designation = name;
	}
	
	public String getPedigree() {
		return pedigree;
	}
	
	public void setPedigree(String pedigree) {
		this.pedigree = pedigree;
	}
	
	

}
