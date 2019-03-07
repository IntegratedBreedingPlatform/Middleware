package org.generationcp.middleware.pojos.germplasm;

import java.io.Serializable;

public class GermplasmParent implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer gid;
	private String name;
	private String pedigree;
	
	public GermplasmParent(Integer gid, String name, String pedigree) {
		super();
		this.gid = gid;
		this.name = name;
		this.pedigree = pedigree;
	}

	public Integer getGid() {
		return gid;
	}
	
	public void setGid(Integer gid) {
		this.gid = gid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPedigree() {
		return pedigree;
	}
	
	public void setPedigree(String pedigree) {
		this.pedigree = pedigree;
	}
	
	

}
