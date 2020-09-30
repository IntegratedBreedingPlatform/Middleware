package org.generationcp.middleware.domain.inventory.manager;


import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class LotUpdateDto {

	private String lotUID;

	private String storageLocationAbbr;

	private String unitName;

	private String notes;

	public String getLotUID() {
		return this.lotUID;
	}

	public void setLotUID(final String lotUID) {
		this.lotUID = lotUID;
	}

	public String getStorageLocationAbbr() {
		return storageLocationAbbr;
	}

	public void setStorageLocationAbbr(final String storageLocationAbbr) {
		this.storageLocationAbbr = storageLocationAbbr;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(final String unitName) {
		this.unitName = unitName;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
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

