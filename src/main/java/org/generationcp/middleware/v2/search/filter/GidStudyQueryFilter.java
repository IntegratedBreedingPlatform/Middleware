package org.generationcp.middleware.v2.search.filter;

public class GidStudyQueryFilter implements StudyQueryFilter {

	private int gid;

	public GidStudyQueryFilter() { }
	
	public GidStudyQueryFilter(int gid) {
		this.gid = gid;
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}
}
