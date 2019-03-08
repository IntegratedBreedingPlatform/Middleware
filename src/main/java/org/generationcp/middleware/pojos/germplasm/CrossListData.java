package org.generationcp.middleware.pojos.germplasm;

import java.util.ArrayList;
import java.util.List;


public class CrossListData {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer entryId;
	private Integer gid;
	private String designation;
	private String seedSource;
	private String breedingMethodName = "";
	
	
	/**
	 * Female parent (gpid1 in the germplsm table)
	 */
	private GermplasmParent femaleParent;
	
	
	/**
	 * List of Male Parents. The first on the list is the germplasm for germplsm.gpid2. The other male parents come from progntrs table.
	 */
	private List<GermplasmParent> maleParents = new ArrayList<>();

	
	
	
	public Integer getId() {
		return id;
	}




	
	public void setId(Integer id) {
		this.id = id;
	}




	public Integer getEntryId() {
		return entryId;
	}



	
	public void setEntryId(Integer entryId) {
		this.entryId = entryId;
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




	
	public void setDesignation(String designation) {
		this.designation = designation;
	}




	public String getSeedSource() {
		return seedSource;
	}



	
	public void setSeedSource(String seedSource) {
		this.seedSource = seedSource;
	}



	
	public String getBreedingMethodName() {
		return breedingMethodName;
	}



	
	public void setBreedingMethodName(String breedingMethodName) {
		this.breedingMethodName = breedingMethodName;
	}



	public GermplasmParent getFemaleParent() {
		return femaleParent;
	}


	
	public void setFemaleParent(GermplasmParent femaleParent) {
		this.femaleParent = femaleParent;
	}


	
	public List<GermplasmParent> getMaleParents() {
		return maleParents;
	}


	
	public void setMaleParents(List<GermplasmParent> maleParents) {
		this.maleParents = maleParents;
	}
	

	public void addProgenitor(final GermplasmParent parent) {
		this.maleParents.add(parent);
	}
	

}
