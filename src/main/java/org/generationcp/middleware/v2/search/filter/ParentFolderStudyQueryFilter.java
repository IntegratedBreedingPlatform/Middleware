package org.generationcp.middleware.v2.search.filter;

public class ParentFolderStudyQueryFilter implements StudyQueryFilter {

	private int folderId;
	
	public ParentFolderStudyQueryFilter() { }
	
	public ParentFolderStudyQueryFilter(int folderId) {
		this.folderId = folderId;
	}

	public int getFolderId() {
		return folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}	
}
