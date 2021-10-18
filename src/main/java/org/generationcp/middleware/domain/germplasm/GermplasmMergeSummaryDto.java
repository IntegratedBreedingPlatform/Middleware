package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmMergeSummaryDto {

	private long countGermplasmToDelete;
	private long countListsToUpdate;
	private long countStudiesToUpdate;
	private long countPlotsToUpdate;
	private long countLotsToMigrate = 0L;
	private long countLotsToClose = 0L;

	public long getCountGermplasmToDelete() {
		return this.countGermplasmToDelete;
	}

	public void setCountGermplasmToDelete(final long countGermplasmToDelete) {
		this.countGermplasmToDelete = countGermplasmToDelete;
	}

	public long getCountListsToUpdate() {
		return this.countListsToUpdate;
	}

	public void setCountListsToUpdate(final long countListsToUpdate) {
		this.countListsToUpdate = countListsToUpdate;
	}

	public long getCountStudiesToUpdate() {
		return this.countStudiesToUpdate;
	}

	public void setCountStudiesToUpdate(final long countStudiesToUpdate) {
		this.countStudiesToUpdate = countStudiesToUpdate;
	}

	public long getCountPlotsToUpdate() {
		return this.countPlotsToUpdate;
	}

	public void setCountPlotsToUpdate(final long countPlotsToUpdate) {
		this.countPlotsToUpdate = countPlotsToUpdate;
	}

	public long getCountLotsToMigrate() {
		return this.countLotsToMigrate;
	}

	public void setCountLotsToMigrate(final long countLotsToMigrate) {
		this.countLotsToMigrate = countLotsToMigrate;
	}

	public long getCountLotsToClose() {
		return this.countLotsToClose;
	}

	public void setCountLotsToClose(final long countLotsToClose) {
		this.countLotsToClose = countLotsToClose;
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
