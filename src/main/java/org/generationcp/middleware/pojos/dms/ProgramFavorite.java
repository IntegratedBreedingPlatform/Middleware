
package org.generationcp.middleware.pojos.dms;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "program_favorites")
public class ProgramFavorite {

	public enum FavoriteType {

		LOCATION,
		METHODS,
		VARIABLES;

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer programFavoriteId;

	@Basic(optional = false)
	@Column(name = "program_uuid")
	private String uniqueID;

	@Basic(optional = false)
	@Column(name = "entity_type")
	@Enumerated(EnumType.STRING)
	private FavoriteType entityType;

	@Basic(optional = false)
	@Column(name = "entity_id")
	private Integer entityId;

	public ProgramFavorite() {
	}

	public ProgramFavorite(final String uniqueID, final FavoriteType favoriteType, final Integer entityId) {
		this.uniqueID = uniqueID;
		this.entityType = favoriteType;
		this.entityId = entityId;
	}

	public Integer getProgramFavoriteId() {
		return this.programFavoriteId;
	}

	public FavoriteType getEntityType() {
		return this.entityType;
	}

	public void setEntityType(final FavoriteType entityType) {
		this.entityType = entityType;
	}

	public Integer getEntityId() {
		return this.entityId;
	}

	public void setEntityId(final Integer entityId) {
		this.entityId = entityId;
	}

	public String getUniqueID() {
		return this.uniqueID;
	}

	public void setUniqueID(final String uniqueID) {
		this.uniqueID = uniqueID;
	}

}
