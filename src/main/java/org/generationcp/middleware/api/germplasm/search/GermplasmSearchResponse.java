package org.generationcp.middleware.api.germplasm.search;

import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.pojos.Germplasm;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Map;

@AutoProperty
public class GermplasmSearchResponse {

	// canonical columns

	private int gid;
	private String groupId;
	private String names;
	private String methodName;
	private String locationName;
	private String stockIds;
	private String availableBalance;

	// added columns

	private String germplasmDate;
	private String methodCode;
	private String methodNumber;
	private String methodGroup;
	private String germplasmPeferredName;
	private String germplasmPeferredId;
	private String groupSourceGID;
	private String groupSourcePreferredName;
	private String immediateSourceGID;
	private String immediateSourcePreferredName;

	// Pedigree-related fields

	private String femaleParentPreferredID;
	private String femaleParentPreferredName;
	private String maleParentPreferredID;
	private String maleParentPreferredName;
	// a.k.a crossExpansion
	private String pedigreeString;

	// attributes and names maps

	private Map<String, String> attributeTypesValueMap;
	private Map<String, String> nameTypesValueMap;

	public GermplasmSearchResponse() {
	}

	public GermplasmSearchResponse(final Germplasm germplasm) {
		this();

		this.gid = germplasm.getGid();
		this.names = germplasm.getGermplasmNamesString();
		final GermplasmInventory inventoryInfo = germplasm.getInventoryInfo();
		if (inventoryInfo != null) {
			this.stockIds = inventoryInfo.getStockIDs();
			this.availableBalance = inventoryInfo.getAvailable();
		}
		this.methodName = germplasm.getMethodName();
		this.locationName = germplasm.getLocationName();
		this.germplasmDate = germplasm.getGermplasmDate();
		this.methodCode = germplasm.getMethodCode();
		this.methodNumber = germplasm.getMethodNumber();
		this.methodGroup = germplasm.getMethodGroup();
		this.germplasmPeferredName = germplasm.getGermplasmPeferredName();
		this.germplasmPeferredId = germplasm.getGermplasmPeferredId();
		this.groupSourceGID = germplasm.getGroupSourceGID();
		this.groupSourcePreferredName = germplasm.getGroupSourcePreferredName();
		this.immediateSourceGID = germplasm.getImmediateSourceGID();
		this.immediateSourcePreferredName = germplasm.getImmediateSourcePreferredName();

		this.attributeTypesValueMap = germplasm.getAttributeTypesValueMap();
		this.nameTypesValueMap = germplasm.getNameTypesValueMap();
	}

	public int getGid() {
		return this.gid;
	}

	public void setGid(final int gid) {
		this.gid = gid;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(final String groupId) {
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

	public String getStockIds() {
		return this.stockIds;
	}

	public void setStockIds(final String stockIds) {
		this.stockIds = stockIds;
	}

	public String getAvailableBalance() {
		return this.availableBalance;
	}

	public void setAvailableBalance(final String availableBalance) {
		this.availableBalance = availableBalance;
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

	public String getGermplasmPeferredName() {
		return this.germplasmPeferredName;
	}

	public void setGermplasmPeferredName(final String germplasmPeferredName) {
		this.germplasmPeferredName = germplasmPeferredName;
	}

	public String getGermplasmPeferredId() {
		return this.germplasmPeferredId;
	}

	public void setGermplasmPeferredId(final String germplasmPeferredId) {
		this.germplasmPeferredId = germplasmPeferredId;
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

	public String getImmediateSourcePreferredName() {
		return this.immediateSourcePreferredName;
	}

	public void setImmediateSourcePreferredName(final String immediateSourcePreferredName) {
		this.immediateSourcePreferredName = immediateSourcePreferredName;
	}

	public String getFemaleParentPreferredID() {
		return this.femaleParentPreferredID;
	}

	public void setFemaleParentPreferredID(final String femaleParentPreferredID) {
		this.femaleParentPreferredID = femaleParentPreferredID;
	}

	public String getFemaleParentPreferredName() {
		return this.femaleParentPreferredName;
	}

	public void setFemaleParentPreferredName(final String femaleParentPreferredName) {
		this.femaleParentPreferredName = femaleParentPreferredName;
	}

	public String getMaleParentPreferredID() {
		return this.maleParentPreferredID;
	}

	public void setMaleParentPreferredID(final String maleParentPreferredID) {
		this.maleParentPreferredID = maleParentPreferredID;
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
