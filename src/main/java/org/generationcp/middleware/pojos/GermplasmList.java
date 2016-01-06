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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;

/**
 * POJO for listnms table.
 *
 * @author Kevin Manansala, Mark Agarrado
 *
 */

@Entity
@Table(name = "listnms")
@SQLDelete(sql = "UPDATE listnms SET liststatus = 9 WHERE listid = ?")
public class GermplasmList implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FOLDER_TYPE = "FOLDER";
	public static final String LIST_TYPE = "LST";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "listid")
	private Integer id;

	@Column(name = "listname")
	private String name;

	@Column(name = "listdate")
	private Long date;

	@Column(name = "listtype")
	private String type;

	@Column(name = "listuid")
	private Integer userId;

	@Column(name = "listdesc")
	private String description;

	@ManyToOne(targetEntity = GermplasmList.class)
	@JoinColumn(name = "lhierarchy")
	@NotFound(action = NotFoundAction.IGNORE)
	private GermplasmList parent;

	@Column(name = "liststatus")
	private Integer status;

	@Column(name = "sdate")
	private Integer sDate;

	@Column(name = "eDate")
	private Integer eDate;

	@Column(name = "listlocn")
	private Integer listLocation;

	@Column(name = "listref")
	private Integer listRef;

	@Column(name = "projectId")
	private Integer projectId;

	@Column(name = "program_uuid")
	private String programUUID;

	@Column(name = "notes")
	private String notes;

	@OneToMany(mappedBy = "list", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("entryId asc")
	private List<GermplasmListData> listData = new ArrayList<GermplasmListData>();

	public static final String GET_GERMPLASM_LIST_TYPES =
			"SELECT fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, fuid, fdate, scaleid "
					+ "FROM udflds "
					+ "WHERE ftable = 'LISTNMS' AND ftype = 'LISTTYPE' and fcode not in ('NURSERY', 'TRIAL', 'CHECK', 'ADVANCED', 'CROSSES', 'FOLDER')";

	public static final String GET_GERMPLASM_NAME_TYPES =
			"SELECT fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, fuid, fdate, scaleid " + "FROM udflds "
					+ "WHERE ftable = 'NAMES' AND ftype = 'NAME'";

	public static final String SEARCH_FOR_GERMPLASM_LIST =
			"SELECT DISTINCT listnms.* "
					+ "FROM listnms "
					+ "      LEFT JOIN listdata ON (listdata.listid=listnms.listid AND lrstatus!=9) "
					+ "      LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.gid!=germplsm.grplce) "
					+ "WHERE listtype not in ('NURSERY', 'TRIAL', 'CHECK', 'ADVANCED', 'CROSSES') AND liststatus!=9 AND listtype!='FOLDER' AND ((listdata.gid=:gid AND 0!=:gid AND length(listdata.gid)=:gidLength) "
					+ "      OR desig LIKE :q OR listname LIKE :q " + "      OR desig LIKE :qNoSpaces "
					+ "      OR desig LIKE :qStandardized " + ")" + " AND program_uuid = :programUUID";

	public static final String SEARCH_FOR_GERMPLASM_LIST_GID_LIKE =
			"SELECT DISTINCT listnms.* "
					+ "FROM listnms "
					+ "      LEFT JOIN listdata ON (listdata.listid=listnms.listid AND lrstatus!=9) "
					+ "      LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.gid!=germplsm.grplce) "
					+ "WHERE listtype not in ('NURSERY', 'TRIAL', 'CHECK', 'ADVANCED', 'CROSSES') AND liststatus!=9 AND listtype!='FOLDER' AND (listdata.gid LIKE :gid "
					+ "      OR desig LIKE :q OR listname LIKE :q" + "      OR desig LIKE :qNoSpaces "
					+ "      OR desig LIKE :qStandardized " + ")" + " AND program_uuid = :programUUID";

	public static final String SEARCH_FOR_GERMPLASM_LIST_EQUAL =
			"SELECT DISTINCT listnms.* "
					+ "FROM listnms "
					+ "      LEFT JOIN listdata ON (listdata.listid=listnms.listid AND lrstatus!=9) "
					+ "      LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.gid!=germplsm.grplce) "
					+ "WHERE "
					+ " listtype not in ('NURSERY', 'TRIAL', 'CHECK', 'ADVANCED', 'CROSSES') AND liststatus!=9 AND listtype!='FOLDER' AND ((listdata.gid=:gid AND 0!=:gid AND length(listdata.gid)=:gidLength) "
					+ "      OR desig = :q OR listname = :q " + "      OR desig = :qNoSpaces " + "      OR desig = :qStandardized " + ")"
					+ " AND program_uuid = :programUUID";

	public GermplasmList() {

	}

	public GermplasmList(final Integer id) {
		super();
		this.id = id;
	}

	public GermplasmList(final Integer id, final String name, final Long date, final String type, final Integer userId,
			final String description, final GermplasmList parent, final Integer status) {
		super();
		this.id = id;
		this.name = name;
		this.date = date;
		this.type = type;
		this.userId = userId;
		this.description = description;
		this.parent = parent;
		this.status = status;
	}

	public GermplasmList(final Integer id, final String name, final Long date, final String type, final Integer userId,
			final String description, final GermplasmList parent, final Integer status, final String notes) {
		super();
		this.id = id;
		this.name = name;
		this.date = date;
		this.type = type;
		this.userId = userId;
		this.description = description;
		this.parent = parent;
		this.status = status;
		this.notes = notes;
	}

	public GermplasmList(final Integer id, final String name, final Long date, final String type, final Integer userId,
			final String description, final GermplasmList parent, final Integer status, final Integer sDate, final Integer eDate,
			final Integer listLocation, final Integer listRef, final Integer projectId, final String notes,
			final List<GermplasmListData> listData) {
		super();
		this.id = id;
		this.name = name;
		this.date = date;
		this.type = type;
		this.userId = userId;
		this.description = description;
		this.parent = parent;
		this.status = status;
		this.sDate = sDate;
		this.eDate = eDate;
		this.listLocation = listLocation;
		this.listRef = listRef;
		this.projectId = projectId;
		this.notes = notes;
		this.listData = listData;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Long getDate() {
		return this.date;
	}

	public void setDate(final Long date) {
		this.date = date;
	}

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(final Integer userId) {
		this.userId = userId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public GermplasmList getParent() {
		return this.parent;
	}

	public void setParent(final GermplasmList parent) {
		this.parent = parent;
	}

	public String getStatusString() {
		// TODO: make internationalizable
		final List<String> listStatus = new ArrayList<String>();
		final String formattedStatus = String.format("%04d", this.getStatus());

		if (formattedStatus.charAt(0) == '1') {
			listStatus.add("Final");
		}
		if (formattedStatus.charAt(1) == '1') {
			listStatus.add("Locked");
		}
		if (formattedStatus.charAt(2) == '1') {
			listStatus.add("Hidden");
		}
		if (formattedStatus.charAt(3) == '1') {
			listStatus.add("List");
		}
		if (formattedStatus.charAt(3) == '0') {
			listStatus.add("Folder");
		}
		if (formattedStatus.charAt(3) == '9') {
			listStatus.add("Deleted");
		}

		final StringBuilder sb = new StringBuilder();
		for (final String str : listStatus) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(str);
		}

		return sb.toString();
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(final Integer status) {
		this.status = status;
	}

	public Integer getsDate() {
		return this.sDate;
	}

	public void setsDate(final Integer sDate) {
		this.sDate = sDate;
	}

	public Integer geteDate() {
		return this.eDate;
	}

	public void seteDate(final Integer eDate) {
		this.eDate = eDate;
	}

	public Integer getListLocation() {
		return this.listLocation;
	}

	public void setListLocation(final Integer listLocation) {
		this.listLocation = listLocation;
	}

	public Integer getListRef() {
		return this.listRef;
	}

	public void setListRef(final Integer listRef) {
		this.listRef = listRef;
	}

	public Integer getProjectId() {
		return this.projectId;
	}

	public void setProjectId(final Integer projectId) {
		this.projectId = projectId;
	}

	public String getProgramUUID() {
		return this.programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public List<GermplasmListData> getListData() {
		return this.listData;
	}

	public void setListData(final List<GermplasmListData> listData) {
		this.listData.clear();
		this.listData.addAll(listData);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("GermplasmList [id=");
		builder.append(this.id);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", date=");
		builder.append(this.date);
		builder.append(", type=");
		builder.append(this.type);
		builder.append(", userId=");
		builder.append(this.userId);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", status=");
		builder.append(this.status);
		builder.append(", notes=");
		builder.append(this.notes);
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
		final GermplasmList other = (GermplasmList) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public Integer getParentId() {
		return this.getParent() != null ? this.getParent().getId() : null;
	}

	public boolean isFolder() {
		return this.getType() != null && this.getType().equalsIgnoreCase(GermplasmList.FOLDER_TYPE) ? true : false;
	}

	public boolean isList() {
		return this.getType() != null && this.getType().equalsIgnoreCase(GermplasmList.LIST_TYPE) ? true : false;
	}

	public boolean hasParent() {
		return this.getParent() != null ? true : false;
	}

	public boolean isLockedList() {
		return this.getStatus() >= 100;
	}

}
