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

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Class ImportedCross.
 */
public class ImportedCross extends ImportedGermplasm implements Serializable {

	private static final String MULTIPARENT_BEGIN_CHAR = "[";
	private static final String MULTIPARENT_END_CHAR = "]";

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public static final String PLOT_DUPE_PREFIX = "Plot Dupe: ";
	public static final String PEDIGREE_DUPE_PREFIX = "Pedigree Dupe: ";
	public static final String PLOT_RECIP_PREFIX = "Plot Recip: ";
	public static final String PEDIGREE_RECIP_PREFIX = "Pedigree Recip: ";

	public static final String SEED_SOURCE_PENDING = "Pending";

	private String notes;
	private Integer crossingDate;
	private String rawBreedingMethod;

	private ImportedGermplasmParent femaleParent;
	private List<ImportedGermplasmParent> maleParents = new ArrayList<>();

	private String duplicate;
	private String duplicatePrefix;

	private Set<Integer> duplicateEntries;

	/**
	 * Instantiates a new imported germplasm.
	 */
	public ImportedCross() {

	}

	/**
	 * Instantiates a new imported germplasm.
	 *
	 * @param entryNumber the entry number
	 */
	public ImportedCross(final Integer entryNumber) {
		this.setEntryNumber(entryNumber);
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
	 * @param check the check
	 */
	public ImportedCross(final Integer entryNumber, final String desig, final String gid, final String cross, final String source, final String entryCode, final String check) {
		super(entryNumber, desig, gid, cross, source, entryCode, check);
	}

	public ImportedCross(final Integer entryNumber, final String desig, final String gid, final String cross, final String source, final String entryCode, final String check,
			final Integer breedingMethodId) {

		super(entryNumber, desig, gid, cross, source, entryCode, check, breedingMethodId);
	}

	public void setOptionalFields(final String rawBreedingMethod, final Integer crossingDate, final String notes) {
		this.rawBreedingMethod = rawBreedingMethod;
		this.crossingDate = crossingDate;
		this.notes = notes;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ImportedCross [entryNumber=" + this.getEntryNumber() + ", desig=" + this.getDesig() + ", femaleParent=" + this.femaleParent
				+ ", maleParents=" + this.maleParents + ", gid=" + this.getGid() + ", cross=" + this.getCross() + ", source="
				+ this.getSource() + ", entryCode=" + this.getEntryCode() + ", check=" + this.getEntryTypeValue() + ", breedingMethodId="
				+ this.getBreedingMethodId() + ", gpid1=" + this.getGpid1() + ", gpid2=" + this.getGpid2() + ", gnpgs=" + this.getGnpgs()
				+ ", names=" + this.getNames() + "]";
	}

	@Override
	public ImportedCross copy() {
		final ImportedCross rec =
				new ImportedCross(this.getEntryNumber(), this.getDesig(), this.getGid(), this.getCross(), this.getSource(),
						this.getEntryCode(), this.getEntryTypeValue(), this.getBreedingMethodId());

		rec.setGpid1(this.getGpid1());
		rec.setGpid2(this.getGpid2());
		rec.setGnpgs(this.getGnpgs());
		rec.setNames(this.getNames());
		rec.setEntryTypeCategoricalID(this.getEntryTypeCategoricalID());
		rec.setEntryTypeName(this.getEntryTypeName());
		rec.setIndex(this.getIndex());
		rec.setFemaleParent(this.femaleParent);
		rec.setMaleParents(this.maleParents);
		return rec;
	}

	public String getFemaleDesignation() {
		return this.femaleParent.getDesignation();
	}

	public List<String> getMaleDesignations() {
		return this.maleParents.stream().map(ImportedGermplasmParent::getDesignation).collect(Collectors.toList());
	}

	private String getMaleParentsValue(final List<String> list) {
		if (list.size() == 1) {
			return list.get(0);
		}
		return MULTIPARENT_BEGIN_CHAR + StringUtils.join(list, ",") + MULTIPARENT_END_CHAR;
	}

	public String getMaleDesignationsAsString() {
		return this.getMaleParentsValue(this.getMaleDesignations());
	}

	public String getFemaleGid() {
		return this.femaleParent.getGid().toString();
	}

	public List<Integer> getMaleGids() {
		return this.maleParents.stream().map(ImportedGermplasmParent::getGid).collect(Collectors.toList());
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public Integer getCrossingDate() {
		return this.crossingDate;
	}

	public void setCrossingDate(final Integer crossingDate) {
		this.crossingDate = crossingDate;
	}

	/**
	 * Retrieves the breeding method of this cross. Note that this breeding method is unprocessed, its possible that this might not exists
	 * in the methods table at all to process the method, retrieve the method from the database and set this.breedingMethodId
	 *
	 * @return
	 */
	public String getRawBreedingMethod() {
		return this.rawBreedingMethod;
	}

	public void setRawBreedingMethod(final String rawBreedingMethod) {
		this.rawBreedingMethod = rawBreedingMethod;
	}

	public String getDuplicate() {
		return this.duplicate;
	}

	public void setDuplicate(final String duplicate) {
		this.duplicate = duplicate;
	}

	public Integer getFemalePlotNo() {
		return this.femaleParent.getPlotNo();
	}

	public List<Integer> getMalePlotNos() {
		return this.maleParents.stream().map(ImportedGermplasmParent::getPlotNo).collect(Collectors.toList());
	}

	public boolean isPedigreeDupe() {
		return ImportedCross.PEDIGREE_DUPE_PREFIX.equals(this.duplicatePrefix);
	}

	public boolean isPlotDupe() {
		return ImportedCross.PLOT_DUPE_PREFIX.equals(this.duplicatePrefix);
	}

	public boolean isPedigreeRecip() {
		return ImportedCross.PEDIGREE_RECIP_PREFIX.equals(this.duplicatePrefix);
	}

	public boolean isPlotRecip() {
		return ImportedCross.PLOT_RECIP_PREFIX.equals(this.duplicatePrefix);
	}

	public Set<Integer> getDuplicateEntries() {
		return this.duplicateEntries;
	}

	public void setDuplicateEntries(final Set<Integer> duplicateEntries) {
		this.duplicateEntries = duplicateEntries;
	}

	public String getDuplicatePrefix() {
		return this.duplicatePrefix;
	}

	public void setDuplicatePrefix(final String duplicatePrefix) {
		this.duplicatePrefix = duplicatePrefix;
	}

	public String getFemaleStudyName() {
		return this.femaleParent.getStudyName();
	}

	public String getMaleStudyName() {
		if (!CollectionUtils.isEmpty(this.maleParents)) {
			return this.maleParents.get(0).getStudyName();
		}
		return "";
	}

	public boolean isBreedingMethodInformationAvailable() {
		return ((this.getBreedingMethodId() != null && this.getBreedingMethodId() != 0) || !StringUtils.isEmpty(this.getRawBreedingMethod()));
	}

	public String getFemalePedigree() {
	  return this.femaleParent.getPedigree();
	}

	public List<String> getMalePedigree() {
		return this.maleParents.stream().map(ImportedGermplasmParent::getPedigree).collect(Collectors.toList());
	}

	public String getFemaleCross() {
		return this.femaleParent.getCross();
	}

	public List<String> getMaleCross() {
		return this.maleParents.stream().map(ImportedGermplasmParent::getCross).collect(Collectors.toList());
	}

	public ImportedGermplasmParent getFemaleParent() {
		return this.femaleParent;
	}


	public void setFemaleParent(final ImportedGermplasmParent femaleParent) {
		this.femaleParent = femaleParent;
	}


	public List<ImportedGermplasmParent> getMaleParents() {
		return this.maleParents;
	}

	public void setMaleParents(final List<ImportedGermplasmParent> maleParents) {
		this.maleParents = maleParents;
	}

	public void setMaleStudyname(final String maleStudyName) {
		for (final ImportedGermplasmParent maleParent : this.maleParents) {
			maleParent.setStudyName(maleStudyName);
		}
	}

	public Boolean isPolyCross() {
		return this.maleParents.size() > 1;
	}
}
