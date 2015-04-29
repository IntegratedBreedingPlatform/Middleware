package org.generationcp.middleware.pojos;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "listdata_project")
public class ListDataProject implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @Basic(optional = false)
    @Column(name = "listdata_project_id")
	private Integer listDataProjectId;
	
    @ManyToOne(targetEntity = GermplasmList.class)
    @JoinColumn(name = "list_id", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private GermplasmList list;

    @Basic(optional = false)
    @Column(name = "germplasm_id")
    private Integer germplasmId;
    
    @Basic(optional = false)
    @Column(name = "check_type")
    private Integer checkType;

    @Basic(optional = false)
    @Column(name = "entry_id")
    private Integer entryId;

    @Basic(optional = false)
    @Column(name = "entry_code")
    private String entryCode;

    @Column(name = "seed_source")
    private String seedSource;

    @Basic(optional = false)
    @Column(name = "designation")
    private String designation;

    @Column(name = "group_name")
    private String groupName;
    
    @Column(name = "duplicate_notes")
    private String duplicate;
   
    
    /***
     * The following will only be field when getListDataProjectWithParents() is called,
     * otherwise, it will always be null
     */
    @Transient
    private String femaleParent = null;
    
    @Transient
    private Integer fgid = null;
    
    @Transient
    private String maleParent = null;
    
    @Transient
    private Integer mgid = null;

	/**
	 * @return the listDataProjectId
	 */
	public Integer getListDataProjectId() {
		return listDataProjectId;
	}

	/**
	 * @param listDataProjectId the listDataProjectId to set
	 */
	public void setListDataProjectId(Integer listDataProjectId) {
		this.listDataProjectId = listDataProjectId;
	}

	/**
	 * @return the list
	 */
	public GermplasmList getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(GermplasmList list) {
		this.list = list;
	}

	/**
	 * @return the germplasmId
	 */
	public Integer getGermplasmId() {
		return germplasmId;
	}

	/**
	 * @param germplasmId the germplasmId to set
	 */
	public void setGermplasmId(Integer germplasmId) {
		this.germplasmId = germplasmId;
	}

	/**
	 * @return the checkType
	 */
	public Integer getCheckType() {
		return checkType;
	}

	/**
	 * @param checkType the checkType to set
	 */
	public void setCheckType(Integer checkType) {
		this.checkType = checkType;
	}

	/**
	 * @return the entryId
	 */
	public Integer getEntryId() {
		return entryId;
	}

	/**
	 * @param entryId the entryId to set
	 */
	public void setEntryId(Integer entryId) {
		this.entryId = entryId;
	}

	/**
	 * @return the entryCode
	 */
	public String getEntryCode() {
		return entryCode;
	}

	/**
	 * @param entryCode the entryCode to set
	 */
	public void setEntryCode(String entryCode) {
		this.entryCode = entryCode;
	}

	/**
	 * @return the seedSource
	 */
	public String getSeedSource() {
		return seedSource;
	}

	/**
	 * @param seedSource the seedSource to set
	 */
	public void setSeedSource(String seedSource) {
		this.seedSource = seedSource;
	}

	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
 
	public String getDuplicate() {
		return duplicate;
	}

	public void setDuplicate(String duplicate) {
		this.duplicate = duplicate;
	}

	public String getFemaleParent() {
		return femaleParent;
	}

	public void setFemaleParent(String femaleParent) {
		this.femaleParent = femaleParent;
	}

	public Integer getFgid() {
		return fgid;
	}

	public void setFgid(Integer fgid) {
		this.fgid = fgid;
	}

	public String getMaleParent() {
		return maleParent;
	}

	public void setMaleParent(String maleParent) {
		this.maleParent = maleParent;
	}

	public Integer getMgid() {
		return mgid;
	}

	public void setMgid(Integer mgid) {
		this.mgid = mgid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ListDataProject [listDataProjectId=" + listDataProjectId
				+ ", list=" + list + ", germplasmId=" + germplasmId
				+ ", checkType=" + checkType + ", entryId=" + entryId
				+ ", entryCode=" + entryCode + ", seedSource=" + seedSource
				+ ", designation=" + designation + ", groupName=" + groupName
				+ ", duplicate=" + duplicate + "]";
	}

}
