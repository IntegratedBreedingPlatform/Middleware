package org.generationcp.middleware.api.program;

import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class ProgramFavoriteDTO {

	private Integer programFavoriteId;
	private ProgramFavorite.FavoriteType entityType;
	private Integer entityId;
	private String programUUID;

	public ProgramFavoriteDTO() {
	}

	public ProgramFavoriteDTO(final Integer programFavoriteId, final ProgramFavorite.FavoriteType entityType,
			final Integer entityId, final String programUUID) {
		this.programFavoriteId = programFavoriteId;
		this.entityType = entityType;
		this.entityId = entityId;
		this.programUUID = programUUID;
	}

	public Integer getProgramFavoriteId() {
		return programFavoriteId;
	}

	public void setProgramFavoriteId(final Integer programFavoriteId) {
		this.programFavoriteId = programFavoriteId;
	}

	public ProgramFavorite.FavoriteType getEntityType() {
		return entityType;
	}

	public void setEntityType(final ProgramFavorite.FavoriteType entityType) {
		this.entityType = entityType;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(final Integer entityId) {
		this.entityId = entityId;
	}

	public String getProgramUUID() {
		return programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
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
