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

package org.generationcp.middleware.domain.dms;

import java.util.List;

/**
 * Contains the primary details of a folder - id, name, description.
 *
 * @author Darla Ani
 *
 */
public class FolderReference extends Reference {

	private Integer parentFolderId;

	private List<FolderReference> subFolders;

	public FolderReference(Integer id, String name) {
		super.setId(id);
		super.setName(name);
	}

	public FolderReference(Integer id, String name, String description) {
		this(id, name);
		super.setDescription(description);
	}

	public FolderReference(Integer id, String name, String description, String programUUID) {
		this(id, name, description);
		super.setProgramUUID(programUUID);
	}

	public FolderReference(Integer parentId, Integer id, String name, String description) {
		this(id, name, description);
		this.parentFolderId = parentId;
	}

	public FolderReference(Integer parentId, Integer id, String name, String description, String programUUID) {
		this(id, name, description, programUUID);
		this.parentFolderId = parentId;
	}

	/**
	 * @return the parentFolderId
	 */
	public Integer getParentFolderId() {
		return this.parentFolderId;
	}

	/**
	 * @param parentFolderId the parentFolderId to set
	 */
	public void setParentFolderId(Integer parentFolderId) {
		this.parentFolderId = parentFolderId;
	}

	/**
	 * @return the subFolders
	 */
	public List<FolderReference> getSubFolders() {
		return this.subFolders;
	}

	/**
	 * @param subFolders the subFolders to set
	 */
	public void setSubFolders(List<FolderReference> subFolders) {
		this.subFolders = subFolders;
	}

}
