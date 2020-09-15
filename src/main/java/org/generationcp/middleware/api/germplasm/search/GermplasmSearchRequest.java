package org.generationcp.middleware.api.germplasm.search;

import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@AutoProperty
public class GermplasmSearchRequest {

	public static class IncludePedigree {

		public static enum Type {
			GENERATIVE, DERIVATIVE, BOTH
		}

		private int generationLevel = 1;
		private Type type = Type.GENERATIVE;

		public int getGenerationLevel() {
			return this.generationLevel;
		}

		public void setGenerationLevel(final int generationLevel) {
			this.generationLevel = generationLevel;
		}

		public Type getType() {
			return this.type;
		}

		public void setType(final Type type) {
			this.type = type;
		}
	}

	// filters

	/**
	 * search all names and variations
	 */
	private SqlTextFilter nameFilter;
	private String breedingMethodName;
	private Map<String, String> attributes;
	// TODO complete

	private IncludePedigree includePedigree;
	private boolean includeGroupMembers;

	// added columns
	private List<String> addedColumnsPropertyIds = new LinkedList<>();

	public SqlTextFilter getNameFilter() {
		return this.nameFilter;
	}

	public void setNameFilter(final SqlTextFilter nameFilter) {
		this.nameFilter = nameFilter;
	}

	public String getBreedingMethodName() {
		return this.breedingMethodName;
	}

	public void setBreedingMethodName(final String breedingMethodName) {
		this.breedingMethodName = breedingMethodName;
	}

	public Map<String, String> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(final Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public IncludePedigree getIncludePedigree() {
		return this.includePedigree;
	}

	public void setIncludePedigree(final IncludePedigree includePedigree) {
		this.includePedigree = includePedigree;
	}

	public boolean isIncludeGroupMembers() {
		return this.includeGroupMembers;
	}

	public void setIncludeGroupMembers(final boolean includeGroupMembers) {
		this.includeGroupMembers = includeGroupMembers;
	}

	public List<String> getAddedColumnsPropertyIds() {
		if (this.addedColumnsPropertyIds == null) {
			this.addedColumnsPropertyIds = new ArrayList<>();
		}
		return this.addedColumnsPropertyIds;
	}

	public void setAddedColumnsPropertyIds(final List<String> addedColumnsPropertyIds) {
		this.addedColumnsPropertyIds = addedColumnsPropertyIds;
	}
}
