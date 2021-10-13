package org.generationcp.middleware.api.program;

import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Set;

@AutoProperty
public class ProgramFavoriteRequestDto {

	private ProgramFavorite.FavoriteType favoriteType;
	private Set<Integer> entityIds;

	public ProgramFavorite.FavoriteType getFavoriteType() {
		return this.favoriteType;
	}

	public void setFavoriteType(final ProgramFavorite.FavoriteType favoriteType) {
		this.favoriteType = favoriteType;
	}

	public Set<Integer> getEntityIds() {
		return this.entityIds;
	}

	public void setEntityIds(final Set<Integer> entityIds) {
		this.entityIds = entityIds;
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
