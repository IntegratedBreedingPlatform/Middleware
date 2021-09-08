package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class GermplasmMergeRequestDto {

	private int targetGermplasmId;

	private List<NonSelectedGermplasm> nonSelectedGermplasmList;

	private MergeOptions mergeOptions;

	public int getTargetGermplasmId() {
		return this.targetGermplasmId;
	}

	public void setTargetGermplasmId(final int targetGermplasmId) {
		this.targetGermplasmId = targetGermplasmId;
	}

	public List<NonSelectedGermplasm> getNonSelectedGermplasm() {
		return this.nonSelectedGermplasmList;
	}

	public void setNonSelectedGermplasm(
		final List<NonSelectedGermplasm> nonSelectedGermplasmList) {
		this.nonSelectedGermplasmList = nonSelectedGermplasmList;
	}

	public MergeOptions getMergeOptions() {
		return this.mergeOptions;
	}

	public void setMergeOptions(final MergeOptions mergeOptions) {
		this.mergeOptions = mergeOptions;
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

	@AutoProperty
	public static class NonSelectedGermplasm {

		private Integer germplasmId;

		private boolean migrateLots;

		private boolean closeLots;

		private boolean omit;

		public Integer getGermplasmId() {
			return this.germplasmId;
		}

		public void setGermplasmId(final Integer germplasmId) {
			this.germplasmId = germplasmId;
		}

		public boolean isMigrateLots() {
			return this.migrateLots;
		}

		public void setMigrateLots(final boolean migrateLots) {
			this.migrateLots = migrateLots;
		}

		public boolean isCloseLots() {
			return this.closeLots;
		}

		public void setCloseLots(final boolean closeLots) {
			this.closeLots = closeLots;
		}

		public boolean isOmit() {
			return this.omit;
		}

		public void setOmit(final boolean omit) {
			this.omit = omit;
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


	@AutoProperty
	public static class MergeOptions {

		private boolean migratePassportData;

		private boolean migrateAttributesData;

		private boolean migrateNameTypes;

		public boolean isMigratePassportData() {
			return this.migratePassportData;
		}

		public void setMigratePassportData(final boolean migratePassportData) {
			this.migratePassportData = migratePassportData;
		}

		public boolean isMigrateAttributesData() {
			return this.migrateAttributesData;
		}

		public void setMigrateAttributesData(final boolean migrateAttributesData) {
			this.migrateAttributesData = migrateAttributesData;
		}

		public boolean isMigrateNameTypes() {
			return this.migrateNameTypes;
		}

		public void setMigrateNameTypes(final boolean migrateNameTypes) {
			this.migrateNameTypes = migrateNameTypes;
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
}
