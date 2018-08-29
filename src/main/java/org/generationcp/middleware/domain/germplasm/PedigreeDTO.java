package org.generationcp.middleware.domain.germplasm;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class PedigreeDTO {

	public static class Sibling {
		private Integer germplasmDbId;
		private String defaultDisplayName;

		public Integer getGermplasmDbId() {
			return germplasmDbId;
		}

		public void setGermplasmDbId(final Integer germplasmDbId) {
			this.germplasmDbId = germplasmDbId;
		}

		public String getDefaultDisplayName() {
			return defaultDisplayName;
		}

		public void setDefaultDisplayName(final String defaultDisplayName) {
			this.defaultDisplayName = defaultDisplayName;
		}

	}

	private Integer germplasmDbId;
	private String defaultDisplayName;
	private String pedigree;
	private String crossingPlan;
	private Integer crossingYear;
	private String familyCode;
	private Integer parent1DbId;
	private String parent1Name;
	private String parent1Type;
	private Integer parent2DbId;
	private String parent2Name;
	private String parent2Type;
	private List<Sibling> siblings;

	public int getGermplasmDbId() {
		return germplasmDbId;
	}

	public void setGermplasmDbId(final Integer germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public String getDefaultDisplayName() {
		return defaultDisplayName;
	}

	public void setDefaultDisplayName(final String defaultDisplayName) {
		this.defaultDisplayName = defaultDisplayName;
	}

	public String getPedigree() {
		return pedigree;
	}

	public void setPedigree(final String pedigree) {
		this.pedigree = pedigree;
	}

	public String getCrossingPlan() {
		return crossingPlan;
	}

	public void setCrossingPlan(final String crossingPlan) {
		this.crossingPlan = crossingPlan;
	}

	public Integer getCrossingYear() {
		return crossingYear;
	}

	public void setCrossingYear(final Integer crossingYear) {
		this.crossingYear = crossingYear;
	}

	public String getFamilyCode() {
		return familyCode;
	}

	public void setFamilyCode(final String familyCode) {
		this.familyCode = familyCode;
	}

	public Integer getParent1DbId() {
		return parent1DbId;
	}

	public void setParent1DbId(final Integer parent1DbId) {
		this.parent1DbId = parent1DbId;
	}

	public String getParent1Name() {
		return parent1Name;
	}

	public void setParent1Name(final String parent1Name) {
		this.parent1Name = parent1Name;
	}

	public String getParent1Type() {
		return parent1Type;
	}

	public void setParent1Type(final String parent1Type) {
		this.parent1Type = parent1Type;
	}

	public Integer getParent2DbId() {
		return parent2DbId;
	}

	public void setParent2DbId(final Integer parent2DbId) {
		this.parent2DbId = parent2DbId;
	}

	public String getParent2Name() {
		return parent2Name;
	}

	public void setParent2Name(final String parent2Name) {
		this.parent2Name = parent2Name;
	}

	public String getParent2Type() {
		return parent2Type;
	}

	public void setParent2Type(final String parent2Type) {
		this.parent2Type = parent2Type;
	}

	public List<Sibling> getSiblings() {
		return siblings;
	}

	public void setSiblings(final List<Sibling> siblings) {
		this.siblings = siblings;
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
