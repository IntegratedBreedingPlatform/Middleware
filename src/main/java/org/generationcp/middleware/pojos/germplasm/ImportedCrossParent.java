package org.generationcp.middleware.pojos.germplasm;

import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;

@AutoProperty
public class ImportedCrossParent implements Serializable {

	private Integer plotNo;
	private String designation;
	private Integer gid;

	public ImportedCrossParent() {

	}

	public ImportedCrossParent(final Integer plotNo, final String designation, final Integer gid) {
		this.plotNo = plotNo;
		this.designation = designation;
		this.gid = gid;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public Integer getPlotNo() {
		return plotNo;
	}

	public void setPlotNo(final Integer plotNo) {
		this.plotNo = plotNo;
	}
}
