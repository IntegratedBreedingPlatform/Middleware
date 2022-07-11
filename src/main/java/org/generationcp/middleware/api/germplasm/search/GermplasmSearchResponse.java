package org.generationcp.middleware.api.germplasm.search;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Map;

@AutoProperty
public class GermplasmSearchResponse {

	// canonical columns

	private int gid;
	private String germplasmUUID;
	private Integer groupId;
	private String names;
	private String methodName;
	private String locationName;
	private String locationAbbr;
	// includes 'Mixed'
	private String availableBalance;
	// includes 'Mixed'
	private String unit;
	private Integer lotCount;

	// added columns

	private String germplasmDate;
	private String methodCode;
	private String methodNumber;
	private String methodGroup;
	private String germplasmPreferredName;
	private String germplasmPreferredId;
	private String groupSourceGID;
	private String groupSourcePreferredName;
	private String immediateSourceGID;
	private String immediateSourceName;

	// Pedigree-related fields

	private String femaleParentGID;
	private String femaleParentPreferredName;
	private String maleParentGID;
	private String maleParentPreferredName;
	// a.k.a crossExpansion
	private String pedigreeString;

	// attributes and names maps

	private Map<String, String> attributeTypesValueMap;
	private Map<String, String> nameTypesValueMap;

	private Integer locationId;
	private Integer breedingMethodId;
	private String reference;

	private Boolean hasProgeny;
	private Boolean usedInLockedStudy;
	private Boolean usedInLockedList;

	public GermplasmSearchResponse() {
	}

	public int getGid() {
		return this.gid;
	}

	public void setGid(final int gid) {
		this.gid = gid;
	}

	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(final Integer groupId) {
		this.groupId = groupId;
	}

	public String getNames() {
		return this.names;
	}

	public void setNames(final String names) {
		this.names = names;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public void setMethodName(final String methodName) {
		this.methodName = methodName;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getAvailableBalance() {
		return this.availableBalance;
	}

	public void setAvailableBalance(final String availableBalance) {
		this.availableBalance = availableBalance;
	}

	public String getUnit() {
		return this.unit;
	}

	public void setUnit(final String unit) {
		this.unit = unit;
	}

	public Integer getLotCount() {
		return this.lotCount;
	}

	public void setLotCount(final Integer lotCount) {
		this.lotCount = lotCount;
	}

	public String getGermplasmDate() {
		return this.germplasmDate;
	}

	public void setGermplasmDate(final String germplasmDate) {
		this.germplasmDate = germplasmDate;
	}

	public String getMethodCode() {
		return this.methodCode;
	}

	public void setMethodCode(final String methodCode) {
		this.methodCode = methodCode;
	}

	public String getMethodNumber() {
		return this.methodNumber;
	}

	public void setMethodNumber(final String methodNumber) {
		this.methodNumber = methodNumber;
	}

	public String getMethodGroup() {
		return this.methodGroup;
	}

	public void setMethodGroup(final String methodGroup) {
		this.methodGroup = methodGroup;
	}

	public String getGermplasmPreferredName() {
		return this.germplasmPreferredName;
	}

	public void setGermplasmPreferredName(final String germplasmPreferredName) {
		this.germplasmPreferredName = germplasmPreferredName;
	}

	public String getGermplasmPreferredId() {
		return this.germplasmPreferredId;
	}

	public void setGermplasmPreferredId(final String germplasmPreferredId) {
		this.germplasmPreferredId = germplasmPreferredId;
	}

	public String getGroupSourceGID() {
		return this.groupSourceGID;
	}

	public void setGroupSourceGID(final String groupSourceGID) {
		this.groupSourceGID = groupSourceGID;
	}

	public String getGroupSourcePreferredName() {
		return this.groupSourcePreferredName;
	}

	public void setGroupSourcePreferredName(final String groupSourcePreferredName) {
		this.groupSourcePreferredName = groupSourcePreferredName;
	}

	public String getImmediateSourceGID() {
		return this.immediateSourceGID;
	}

	public void setImmediateSourceGID(final String immediateSourceGID) {
		this.immediateSourceGID = immediateSourceGID;
	}

	public String getImmediateSourceName() {
		return this.immediateSourceName;
	}

	public void setImmediateSourceName(final String immediateSourceName) {
		this.immediateSourceName = immediateSourceName;
	}

	public String getFemaleParentGID() {
		return this.femaleParentGID;
	}

	public void setFemaleParentGID(final String femaleParentGID) {
		this.femaleParentGID = femaleParentGID;
	}

	public String getFemaleParentPreferredName() {
		return this.femaleParentPreferredName;
	}

	public void setFemaleParentPreferredName(final String femaleParentPreferredName) {
		this.femaleParentPreferredName = femaleParentPreferredName;
	}

	public String getMaleParentGID() {
		return this.maleParentGID;
	}

	public void setMaleParentGID(final String maleParentGID) {
		this.maleParentGID = maleParentGID;
	}

	public String getMaleParentPreferredName() {
		return this.maleParentPreferredName;
	}

	public void setMaleParentPreferredName(final String maleParentPreferredName) {
		this.maleParentPreferredName = maleParentPreferredName;
	}

	public String getPedigreeString() {
		return this.pedigreeString;
	}

	public void setPedigreeString(final String pedigreeString) {
		this.pedigreeString = pedigreeString;
	}

	public Map<String, String> getAttributeTypesValueMap() {
		return this.attributeTypesValueMap;
	}

	public void setAttributeTypesValueMap(final Map<String, String> attributeTypesValueMap) {
		this.attributeTypesValueMap = attributeTypesValueMap;
	}

	public Map<String, String> getNameTypesValueMap() {
		return this.nameTypesValueMap;
	}

	public void setNameTypesValueMap(final Map<String, String> nameTypesValueMap) {
		this.nameTypesValueMap = nameTypesValueMap;
	}

	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
	}

	public Integer getBreedingMethodId() {
		return this.breedingMethodId;
	}

	public void setBreedingMethodId(final Integer breedingMethodId) {
		this.breedingMethodId = breedingMethodId;
	}

	public String getGermplasmUUID() {
		return this.germplasmUUID;
	}

	public void setGermplasmUUID(final String germplasmUUID) {
		this.germplasmUUID = germplasmUUID;
	}

	public String getLocationAbbr() {
		return this.locationAbbr;
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public void setLocationAbbr(final String locationAbbr) {
		this.locationAbbr = locationAbbr;
	}

	public Boolean getHasProgeny() {
		return this.hasProgeny;
	}

	public void setHasProgeny(final Boolean hasProgeny) {
		this.hasProgeny = hasProgeny;
	}

	public Boolean getUsedInLockedStudy() {
		return this.usedInLockedStudy;
	}

	public void setUsedInLockedStudy(final Boolean usedInLockedStudy) {
		this.usedInLockedStudy = usedInLockedStudy;
	}

	public Boolean getUsedInLockedList() {
		return this.usedInLockedList;
	}

	public void setUsedInLockedList(final Boolean usedInLockedList) {
		this.usedInLockedList = usedInLockedList;
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
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}
}
