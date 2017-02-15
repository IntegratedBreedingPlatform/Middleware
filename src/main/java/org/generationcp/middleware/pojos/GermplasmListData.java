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

package org.generationcp.middleware.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.interfaces.GermplasmExportSource;
import org.generationcp.middleware.util.Debug;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * POJO for listdata table.
 * 
 * 
 */
@NamedQueries({@NamedQuery(name = "deleteGermplasmListDataByListId", query = "DELETE FROM GermplasmListData WHERE list = :listId"),})
@Entity
@Table(name = "listdata")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "listdata")
public class GermplasmListData implements Serializable, GermplasmExportSource {

	private static final long serialVersionUID = 1L;

	// string contants for name of queries
	public static final String DELETE_BY_LIST_ID = "deleteGermplasmListDataByListId";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "lrecid")
	private Integer id;

	@ManyToOne(targetEntity = GermplasmList.class)
	@JoinColumn(name = "listid", nullable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	private GermplasmList list;

	@Basic(optional = false)
	@Column(name = "gid")
	private Integer gid;

	@Basic(optional = false)
	@Column(name = "entryid")
	private Integer entryId;

	@Basic(optional = false)
	@Column(name = "entrycd")
	private String entryCode;

	@Column(name = "source")
	private String seedSource;

	@Basic(optional = false)
	@Column(name = "desig")
	private String designation;

	@Column(name = "grpname")
	private String groupName;

	@Basic(optional = false)
	@Column(name = "lrstatus")
	private Integer status;

	@Basic(optional = true)
	@Column(name = "llrecid")
	private Integer localRecordId;

	@OneToMany(mappedBy = "listData", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ListDataProperty> properties = new ArrayList<ListDataProperty>();

	@OneToOne
	@JoinColumn(name = "gid", nullable = false, insertable = false, updatable = false)
	private Germplasm germplasm;

	@Transient
	private ListDataInventory inventoryInfo;

	@Transient
	private String notes;

	@Transient
	private Integer crossingDate;

	/***
	 * The following will only be field when we are getting the parents, otherwise, they won't be set
	 */

	/**
	 * The preferred name of the female parent (nval in the database table)
	 */
	@Transient
	private String femaleParent = null;

	/**
	 * GID of the female parent (gpid1 in the database table)
	 */
	@Transient
	private Integer fgid = null;

	/**
	 * The preferred name of the male parent (nval in the database table)
	 */
	@Transient
	private String maleParent = "";

	/**
	 * GID of the male parent (gpid2 in the database table)
	 */
	@Transient
	private Integer mgid = null;

	/**
	 * The Group ID of the germplasm. It is mapped to germplsm.mgid column in the database.
	 */
	@Transient
	private Integer groupId = 0;

	@Transient
	private String breedingMethodName = "";

	@Transient
	private String stockIDs = "";

  	@Transient
  	private String femalePedigree;

  	@Transient
  	private String malePedigree;

	public GermplasmListData() {

	}

	public GermplasmListData(final Integer id) {
		super();
		this.id = id;
	}

	public GermplasmListData(final Integer id, final GermplasmList list, final Integer gid, final Integer entryId, final String entryCode,
			final String seedSource, final String designation, final String groupName, final Integer status, final Integer localRecordId) {
		super();
		this.id = id;
		this.list = list;
		this.gid = gid;
		this.entryId = entryId;
		this.entryCode = entryCode;
		this.seedSource = seedSource;
		this.designation = designation;
		this.groupName = groupName;
		this.status = status;
		this.localRecordId = localRecordId;
	}

	public GermplasmListData(final Integer id, final GermplasmList list, final Integer gid, final Integer entryId, final String entryCode,
			final String seedSource, final String designation, final String groupName, final Integer status, final Integer localRecordId,
			final String notes, final Integer crossingDate) {
		this(id, list, gid, entryId, entryCode, seedSource, designation, groupName, status, localRecordId);
		this.notes = notes;
		this.crossingDate = crossingDate;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public GermplasmList getList() {
		return this.list;
	}

	public void setList(final GermplasmList list) {
		this.list = list;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	@Override
	public Integer getEntryId() {
		return this.entryId;
	}

	public void setEntryId(final Integer entryId) {
		this.entryId = entryId;
	}

	@Override
	public String getEntryCode() {
		return this.entryCode;
	}

	public void setEntryCode(final String entryCode) {
		this.entryCode = entryCode;
	}

	@Override
	public String getSeedSource() {
		return this.seedSource;
	}

	public void setSeedSource(final String seedSource) {
		this.seedSource = seedSource;
	}

	@Override
	public String getDesignation() {
		return this.designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	@Override
	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(final String groupName) {
		this.groupName = groupName;
	}

	public Integer getStatus() {
		return this.status;
	}

	public String getStatusString() {
		// TODO: make internationalizable
		if (this.getStatus().equals(0)) {
			return "Active";
		} else if (this.getStatus().equals(9)) {
			return "Deleted";
		} else {
			return "";
		}
	}

	public void setStatus(final Integer status) {
		this.status = status;
	}

	public Integer getLocalRecordId() {
		return this.localRecordId;
	}

	public void setLocalRecordId(final Integer localRecordId) {
		this.localRecordId = localRecordId;
	}

	public List<ListDataProperty> getProperties() {
		return this.properties;
	}

	public void setProperties(final List<ListDataProperty> properties) {
		this.properties = properties;
	}

	public ListDataInventory getInventoryInfo() {
		return this.inventoryInfo;
	}

	public void setInventoryInfo(final ListDataInventory inventoryInfo) {
		this.inventoryInfo = inventoryInfo;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Integer getCrossingDate() {
		return crossingDate;
	}

	public void setCrossingDate(Integer crossingDate) {
		this.crossingDate = crossingDate;
	}

	/**
	 * @return the germplasm
	 */
	public Germplasm getGermplasm() {
		return this.germplasm;
	}

	/**
	 * @param germplasm the germplasm to set
	 */
	public void setGermplasm(final Germplasm germplasm) {
		this.germplasm = germplasm;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("GermplasmListData [id=");
		builder.append(this.id);
		builder.append(", gid=");
		builder.append(this.gid);
		builder.append(", entryId=");
		builder.append(this.entryId);
		builder.append(", entryCode=");
		builder.append(this.entryCode);
		builder.append(", seedSource=");
		builder.append(this.seedSource);
		builder.append(", designation=");
		builder.append(this.designation);
		builder.append(", groupName=");
		builder.append(this.groupName);
		builder.append(", status=");
		builder.append(this.status);
		builder.append(", localRecordId=");
		builder.append(this.localRecordId);
		builder.append(", notes=");
		builder.append(this.notes);
		builder.append(", crossingDate=");
		builder.append(this.crossingDate);
		builder.append(", femalePedigree=");
		builder.append(this.femalePedigree);
		builder.append(", malePedigree=");
		builder.append(this.malePedigree);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.id == null ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final GermplasmListData other = (GermplasmListData) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public void print(final int indent) {
		Debug.println(indent, this.toString());
		if (this.properties != null) {
			for (final ListDataProperty property : this.properties) {
				property.print(indent + 3);
			}
		}
	}

	@Override
	public Integer getGermplasmId() {
		return this.gid;
	}

	@Override
	public Integer getCheckType() {
		return null;
	}

	@Override
	public String getFemaleParent() {
		return this.femaleParent;
	}

	@Override
	public Integer getFgid() {
		return this.fgid;
	}

	@Override
	public String getMaleParent() {
		return this.maleParent;
	}

	@Override
	public Integer getMgid() {
		return this.mgid;
	}

	@Override
	public String getCheckTypeDescription() {
		return null;
	}

	@Override
	public String getStockIDs() {
		return this.stockIDs;
	}

	@Override
	public String getSeedAmount() {
		return "";
	}

	public void setFemaleParent(final String femaleParent) {
		this.femaleParent = femaleParent;
	}

	public void setFgid(final Integer fgid) {
		this.fgid = fgid;
	}

	public void setMaleParent(final String maleParent) {
		this.maleParent = maleParent;
	}

	public void setMgid(final Integer mgid) {
		this.mgid = mgid;
	}

	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(final Integer groupId) {
		this.groupId = groupId;
	}
	public String getBreedingMethodName() {
		return breedingMethodName;
	}

	public void setBreedingMethodName(String breedingMethodName) {
		this.breedingMethodName = breedingMethodName;
	}

	public void setStockIDs(String stockIDs) {
		this.stockIDs = stockIDs;
	}

	public String getFemalePedigree() {
	  return femalePedigree;
	}

	public void setFemalePedigree(final String femalePedigree) {
	  this.femalePedigree = femalePedigree;
	}

	public String getMalePedigree() {
	  return malePedigree;
	}

	public void setMalePedigree(final String malePedigree) {
	  this.malePedigree = malePedigree;
	}
}
