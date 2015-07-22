
package org.generationcp.middleware.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "listdata_project")
public class ListDataProject implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String PEDIGREE_DUPE = "Pedigree Dupe";
	public static final String PLOT_DUPE = "Plot Dupe";
	public static final String PEDIGREE_RECIP = "Pedigree Recip";
	public static final String PLOT_RECIP = "Plot Recip";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	 * The following will only be field when getListDataProjectWithParents() is called, otherwise, it will always be null
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
		return this.listDataProjectId;
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
		return this.list;
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
		return this.germplasmId;
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
		return this.checkType;
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
		return this.entryId;
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
		return this.entryCode;
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
		return this.seedSource;
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
		return this.designation;
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
		return this.groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDuplicate() {
		return this.duplicate;
	}

	public void setDuplicate(String duplicate) {
		this.duplicate = duplicate;
	}

	public String getFemaleParent() {
		return this.femaleParent;
	}

	public void setFemaleParent(String femaleParent) {
		this.femaleParent = femaleParent;
	}

	public Integer getFgid() {
		return this.fgid;
	}

	public void setFgid(Integer fgid) {
		this.fgid = fgid;
	}

	public String getMaleParent() {
		return this.maleParent;
	}

	public void setMaleParent(String maleParent) {
		this.maleParent = maleParent;
	}

	public Integer getMgid() {
		return this.mgid;
	}

	public void setMgid(Integer mgid) {
		this.mgid = mgid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ListDataProject [listDataProjectId=" + this.listDataProjectId + ", list=" + this.list + ", germplasmId=" + this.germplasmId
				+ ", checkType=" + this.checkType + ", entryId=" + this.entryId + ", entryCode=" + this.entryCode + ", seedSource="
				+ this.seedSource + ", designation=" + this.designation + ", groupName=" + this.groupName + ", duplicate=" + this.duplicate
				+ "]";
	}

	public Boolean isPedigreeDupe() {
		if (this.duplicate != null) {
			return this.duplicate.contains(ListDataProject.PEDIGREE_DUPE);
		}
		return false;
	}

	public Boolean isPlotDupe() {
		if (this.duplicate != null) {
			return this.duplicate.contains(ListDataProject.PLOT_DUPE);
		}
		return false;
	}

	public Boolean isPedigreeRecip() {
		if (this.duplicate != null) {
			return this.duplicate.contains(ListDataProject.PEDIGREE_RECIP);
		}
		return false;
	}

	public Boolean isPlotRecip() {
		if (this.duplicate != null) {
			return this.duplicate.contains(ListDataProject.PLOT_RECIP);
		}
		return false;
	}

	public List<Integer> parsePedigreeDupeInformation() {
		List<Integer> returnVal = new ArrayList<>();

		if (!this.isPedigreeDupe()) {
			return returnVal;
		}

		return this.parseDuplicateString(ListDataProject.PEDIGREE_DUPE);
	}

	public List<Integer> parsePlotDupeInformation() {
		if (!this.isPlotDupe()) {
			return new ArrayList<Integer>();
		}

		return this.parseDuplicateString(ListDataProject.PLOT_DUPE);
	}

	public List<Integer> parsePlotReciprocalInformation() {
		if (!this.isPlotRecip()) {
			return new ArrayList<Integer>();
		}

		return this.parseDuplicateString(ListDataProject.PLOT_RECIP);
	}

	public List<Integer> parsePedigreeReciprocalInformation() {
		if (!this.isPedigreeRecip()) {
			return new ArrayList<Integer>();
		}

		return this.parseDuplicateString(ListDataProject.PEDIGREE_RECIP);
	}

	protected List<Integer> parseDuplicateString(String forRemoval) {
		String temp = this.duplicate.replace(forRemoval, "");
		temp = temp.replace(":", "");
		temp = temp.trim();

		List<Integer> returnVal = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(temp, ",");

		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();

			if (token.length() == 0) {
				continue;
			}

			returnVal.add(Integer.valueOf(token));

		}

		return returnVal;
	}

}
