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

package org.generationcp.middleware.domain.search.filter;

public class ParentFolderStudyQueryFilter implements StudyQueryFilter {

	private int folderId;

	public ParentFolderStudyQueryFilter() {
	}

	public ParentFolderStudyQueryFilter(int folderId) {
		this.folderId = folderId;
	}

	public int getFolderId() {
		return this.folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}
}
