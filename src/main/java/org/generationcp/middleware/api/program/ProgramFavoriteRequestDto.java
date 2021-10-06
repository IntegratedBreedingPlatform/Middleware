package org.generationcp.middleware.api.program;

import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Set;

@AutoProperty
public class ProgramFavoriteRequestDto {

	private ProgramFavorite.FavoriteType favoriteType;
	private Set<Integer> entityId;

	public ProgramFavorite.FavoriteType getFavoriteType() {
		return this.favoriteType;
	}

	public void setFavoriteType(final ProgramFavorite.FavoriteType favoriteType) {
		this.favoriteType = favoriteType;
	}

	public Set<Integer> getEntityId() {
		return this.entityId;
	}

	public void setEntityId(final Set<Integer> entityId) {
		this.entityId = entityId;
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
