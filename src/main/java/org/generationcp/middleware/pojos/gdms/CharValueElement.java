package org.generationcp.middleware.pojos.gdms;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

/**
 * Created by clarysabel on 11/21/17.
 */
@AutoProperty
public class CharValueElement {

	private String sampleUID;

	private Integer accessionId;

	private String sampleName;

	private Integer gid;

	private String designation;

	private Integer sampleNo;

	private Integer markerId;

	private String markerName;

	private String charValue;

	private Integer datasetId;

	public String getSampleUID() {
		return sampleUID;
	}

	public void setSampleUID(final String sampleUID) {
		this.sampleUID = sampleUID;
	}

	public Integer getAccessionId() {
		return accessionId;
	}

	public void setAccessionId(final Integer accessionId) {
		this.accessionId = accessionId;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(final String sampleName) {
		this.sampleName = sampleName;
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

	public Integer getSampleNo() {
		return sampleNo;
	}

	public void setSampleNo(final Integer sampleNo) {
		this.sampleNo = sampleNo;
	}

	public String getMarkerName() {
		return markerName;
	}

	public void setMarkerName(final String markerName) {
		this.markerName = markerName;
	}

	public String getCharValue() {
		return charValue;
	}

	public void setCharValue(final String charValue) {
		this.charValue = charValue;
	}

	public Integer getMarkerId() {
		return markerId;
	}

	public void setMarkerId(final Integer markerId) {
		this.markerId = markerId;
	}

	public Integer getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(final Integer datasetId) {
		this.datasetId = datasetId;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(Object o) {
		return Pojomatic.equals(this, o);
	}

}
