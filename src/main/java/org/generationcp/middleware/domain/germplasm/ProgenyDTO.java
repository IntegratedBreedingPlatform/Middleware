package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class ProgenyDTO {

	public static class Progeny {

		private int germplasmDbId;
		private String defaultDisplayName;
		private String parentType;

		public int getGermplasmDbId() {
			return germplasmDbId;
		}

		public void setGermplasmDbId(final int germplasmDbId) {
			this.germplasmDbId = germplasmDbId;
		}

		public String getDefaultDisplayName() {
			return defaultDisplayName;
		}

		public void setDefaultDisplayName(final String defaultDisplayName) {
			this.defaultDisplayName = defaultDisplayName;
		}

		public String getParentType() {
			return parentType;
		}

		public void setParentType(final String parentType) {
			this.parentType = parentType;
		}
	}


	private int germplasmDbId;
	private String defaultDisplayName;
	private List<Progeny> progeny;

	public int getGermplasmDbId() {
		return germplasmDbId;
	}

	public void setGermplasmDbId(final int germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public String getDefaultDisplayName() {
		return defaultDisplayName;
	}

	public void setDefaultDisplayName(final String defaultDisplayName) {
		this.defaultDisplayName = defaultDisplayName;
	}

	public List<Progeny> getProgeny() {
		return progeny;
	}

	public void setProgeny(final List<Progeny> progeny) {
		this.progeny = progeny;
	}


	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}
