package org.generationcp.middleware.api.germplasm.search;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;
import java.util.Set;

@AutoProperty
public class GermplasmAttributeSearchRequest {

	private Set<Integer> gids;

	private String programUUID;

	private List<Integer> variableTypeIds;

	public Set<Integer> getGids() {
		return this.gids;
	}

	public void setGids(final Set<Integer> gids) {
		this.gids = gids;
	}

	public String getProgramUUID() {
		return this.programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public List<Integer> getVariableTypeIds() {
		return this.variableTypeIds;
	}

	public void setVariableTypeIds(final List<Integer> variableTypeIds) {
		this.variableTypeIds = variableTypeIds;
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
