package org.generationcp.middleware.domain.inventory.manager;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@AutoProperty
public class LotMultiUpdateRequestDto {

	@AutoProperty
	public static class LotUpdateDto {

		private String lotUID;

		private String storageLocationAbbr;

		private String unitName;

		private String notes;

		private String newLotUID;

		private Map<String, String> attributes = new HashMap<>();

		public LotUpdateDto(){

		}

		public String getLotUID() {
			return this.lotUID;
		}

		public void setLotUID(final String lotUID) {
			this.lotUID = lotUID;
		}

		public String getStorageLocationAbbr() {
			return this.storageLocationAbbr;
		}

		public void setStorageLocationAbbr(final String storageLocationAbbr) {
			this.storageLocationAbbr = storageLocationAbbr;
		}

		public String getUnitName() {
			return this.unitName;
		}

		public void setUnitName(final String unitName) {
			this.unitName = unitName;
		}

		public String getNotes() {
			return this.notes;
		}

		public void setNotes(final String notes) {
			this.notes = notes;
		}

		public String getNewLotUID() {
			return this.newLotUID;
		}

		public void setNewLotUID(final String newLotUID) {
			this.newLotUID = newLotUID;
		}

		public Map<String, String> getAttributes() {
			return this.attributes;
		}

		public void setAttributes(final Map<String, String> attributes) {
			this.attributes = attributes;
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


	private List<LotUpdateDto> lotList = new LinkedList<>();

	public List<LotUpdateDto> getLotList() {
		return this.lotList;
	}

	public LotMultiUpdateRequestDto(){

	}

	public void setLotList(final List<LotUpdateDto> lotList) {
		this.lotList = lotList;
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
