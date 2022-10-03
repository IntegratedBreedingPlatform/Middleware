/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.ruleengine.pojo;

import org.generationcp.middleware.pojos.Name;

import java.io.Serializable;
import java.util.List;

/**
 * The Class ImportedGermplasm.
 */
public class ImportedGermplasm implements Serializable {

	public static final String GID_PENDING = "Pending";

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private Integer id;

	/** The entry number. */
	private Integer entryNumber;

	/** The desig. */
	private String desig;

	/** The gid. */
	private String gid;

	/** The cross. */
	private String cross;

	/** The source. */
	private String source;

	/** The entry code. */
	private String entryCode;

	/** The check. */
	private String entryTypeValue;

	/** The breeding method id. */
	private Integer breedingMethodId;

	/** The breeding method name. */
	private String breedingMethodName;

	/** Germplasm's GPID1 */
	private Integer gpid1;

	/** Germplasm's GPID2 */
	private Integer gpid2;

	/** Germplasm's Number of Parents */
	private Integer gnpgs;
	
	/** The maintenance group this germplasm belongs to */
	private Integer mgid;

	/** List of Names associated with the Germplasm */
	private List<Name> names;

    private Integer entryTypeCategoricalID;

	private String entryTypeName;

	private Integer index;

	private String groupName;

	private String trialInstanceNumber;
	private String replicationNumber;
    private String plotNumber;

	private String plantNumber;

	/** GroupId associated with Germplsm **/
	private Integer groupId;

	private Integer locationId;

	/**
	 * Instantiates a new imported germplasm.
	 */
	public ImportedGermplasm() {

	}

	/**
	 * Instantiates a new imported germplasm.
	 *
	 * @param entryNumber the entry number
	 * @param desig the desig
	 * @param entryTypeValue the check
	 */
	public ImportedGermplasm(final Integer entryNumber, final String desig, final String entryTypeValue) {
		this.entryNumber = entryNumber;
		this.desig = desig;
		this.entryTypeValue = entryTypeValue;
	}

	/**
	 * Instantiates a new imported germplasm.
	 *
	 * @param entryNumber the entry number
	 * @param desig the desig
	 * @param gid the gid
	 * @param cross the cross
	 * @param source the source
	 * @param entryCode the entry code
	 * @param entryTypeValue the check
	 */
	public ImportedGermplasm(final Integer entryNumber, final String desig, final String gid, final String cross, final String source, final String entryCode, final String entryTypeValue) {
		this.entryNumber = entryNumber;
		this.desig = desig;
		this.gid = gid;
		this.cross = cross;
		this.source = source;
		this.entryCode = entryCode;
		this.entryTypeValue = entryTypeValue;
	}

	public ImportedGermplasm(Integer entryId, String desig, String gid, String cross, String source, String entryCode, String entryTypeValue,
			Integer breedingMethodId) {

		this(entryId, desig, gid, cross, source, entryCode, entryTypeValue);
		this.breedingMethodId = breedingMethodId;
	}

	/**
	 * Gets the gid.
	 *
	 * @return the gid
	 */
	public String getGid() {
		return this.gid;
	}

	/**
	 * Sets the gid.
	 *
	 * @param gid the new gid
	 */
	public void setGid(String gid) {
		this.gid = gid;
	}

	/**
	 * Gets the cross.
	 *
	 * @return the cross
	 */
	public String getCross() {
		return this.cross;
	}

	/**
	 * Sets the cross.
	 *
	 * @param cross the new cross
	 */
	public void setCross(String cross) {
		this.cross = cross;
	}

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * Sets the source.
	 *
	 * @param source the new source
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Gets the entry code.
	 *
	 * @return the entry code
	 */
	public String getEntryCode() {
		return this.entryCode;
	}

	/**
	 * Sets the entry code.
	 *
	 * @param entryCode the new entry code
	 */
	public void setEntryCode(String entryCode) {
		this.entryCode = entryCode;
	}

	/**
	 * Gets the entry number.
	 *
	 * @return the entry number
	 */
	public Integer getEntryNumber() {
		return this.entryNumber;
	}

	/**
	 * Sets the entry number.
	 *
	 * @param entryNumber the new entry number
	 */
	public void setEntryNumber(final Integer entryNumber) {
		this.entryNumber = entryNumber;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	/**
	 * Gets the desig.
	 *
	 * @return the desig
	 */
	public String getDesig() {
		return this.desig;
	}

	/**
	 * Sets the desig.
	 *
	 * @param desig the new desig
	 */
	public void setDesig(String desig) {
		this.desig = desig;
	}

	/**
	 * Gets the check.
	 *
	 * @return the check
	 */
	public String getEntryTypeValue() {
		return this.entryTypeValue;
	}

	/**
	 * Sets the check.
	 *
	 * @param entryTypeValue the new check
	 */
	public void setEntryTypeValue(String entryTypeValue) {
		this.entryTypeValue = entryTypeValue;
	}

	/**
	 * @return the breedingMethodId
	 */
	public Integer getBreedingMethodId() {
		return this.breedingMethodId;
	}

	/**
	 * @param breedingMethodId the breedingMethodId to set
	 */
	public void setBreedingMethodId(Integer breedingMethodId) {
		this.breedingMethodId = breedingMethodId;
	}

	/**
	 * @return the breeding method name
	 */
	public String getBreedingMethodName() {
		return breedingMethodName;
	}

	/**
	 * @param breedingMethodName the breeding method name to set
	 */
	public void setBreedingMethodName(String breedingMethodName) {
		this.breedingMethodName = breedingMethodName;
	}

	/**
	 * @return the gpid1
	 */
	public Integer getGpid1() {
		return this.gpid1;
	}

	/**
	 * @param gpid1 the gpid1 to set
	 */
	public void setGpid1(Integer gpid1) {
		this.gpid1 = gpid1;
	}

	/**
	 * @return the gpid2
	 */
	public Integer getGpid2() {
		return this.gpid2;
	}

	/**
	 * @param gpid2 the gpid2 to set
	 */
	public void setGpid2(Integer gpid2) {
		this.gpid2 = gpid2;
	}

	/**
	 * @return the gnpgs
	 */
	public Integer getGnpgs() {
		return this.gnpgs;
	}

	/**
	 * @param gnpgs the gnpgs to set
	 */
	public void setGnpgs(Integer gnpgs) {
		this.gnpgs = gnpgs;
	}

	
	public Integer getMgid() {
		return mgid;
	}

	
	public void setMgid(Integer mgid) {
		this.mgid = mgid;
	}

	/**
	 * @return the names
	 */
	public List<Name> getNames() {
		return this.names;
	}

	/**
	 * @param names the names to set
	 */
	public void setNames(List<Name> names) {
		this.names = names;
	}

	/**
	 * @return the entryTypeCategoricalID
	 */
	public Integer getEntryTypeCategoricalID() {
		return this.entryTypeCategoricalID;
	}

	/**
	 * @param entryTypeCategoricalID the entryTypeCategoricalID to set
	 */
	public void setEntryTypeCategoricalID(Integer entryTypeCategoricalID) {
		this.entryTypeCategoricalID = entryTypeCategoricalID;
	}

	/**
	 * @return the entryTypeName
	 */
	public String getEntryTypeName() {
		return this.entryTypeName;
	}

	/**
	 * @param entryTypeName the entryTypeName to set
	 */
	public void setEntryTypeName(String entryTypeName) {
		this.entryTypeName = entryTypeName;
	}

	public String getTrialInstanceNumber() {
		return this.trialInstanceNumber;
	}

	public void setTrialInstanceNumber(String trialInstanceNumber) {
		this.trialInstanceNumber = trialInstanceNumber;
	}

	public String getReplicationNumber() {
		return this.replicationNumber;
	}

	public void setReplicationNumber(String replicationNumber) {
		this.replicationNumber = replicationNumber;
	}

    public String getPlotNumber() {
        return plotNumber;
    }

    public void setPlotNumber(String plotNumber) {
        this.plotNumber = plotNumber;
    }

    /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
	@Override
	public String toString() {
		return "ImportedGermplasm [id=" + this.id + ", entryNumber=" + this.entryNumber + ", desig=" + this.desig + ", gid=" + this.gid + ", cross=" + this.cross
				+ ", source=" + this.source + ", entryCode=" + this.entryCode + ", entryTypeValue=" + this.entryTypeValue + ", breedingMethodId="
				+ this.breedingMethodId + ", gpid1=" + this.gpid1 + ", gpid2=" + this.gpid2 + ", gnpgs=" + this.gnpgs + ", names="
				+ this.names + "]";
	}

	public ImportedGermplasm copy() {
		ImportedGermplasm rec =
				new ImportedGermplasm(this.entryNumber, this.desig, this.gid, this.cross, this.source, this.entryCode, this.entryTypeValue,
						this.breedingMethodId);

		rec.setGpid1(this.gpid1);
		rec.setGpid2(this.gpid2);
		rec.setGnpgs(this.gnpgs);
		rec.setNames(this.names);
		rec.setEntryTypeCategoricalID(this.entryTypeCategoricalID);
		rec.setEntryTypeName(this.entryTypeName);
		rec.setIndex(this.index);

		return rec;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getPlantNumber() {
		return plantNumber;
	}

	public void setPlantNumber(final String plantNumber) {
		this.plantNumber = plantNumber;
	}

	public Integer getIndex() {
		return this.index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
	}
}
