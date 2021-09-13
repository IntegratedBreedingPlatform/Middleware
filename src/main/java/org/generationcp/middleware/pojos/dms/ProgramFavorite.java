
package org.generationcp.middleware.pojos.dms;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "program_favorites")
public class ProgramFavorite {

	public enum FavoriteType {
		LOCATION("LOCATION"), METHOD("METHODS"), VARIABLE("VARIABLES");

		private final String name;

		FavoriteType(final String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static FavoriteType getByName(final String name) {
			for (final FavoriteType type : FavoriteType.values()) {
				if (type.getName().equalsIgnoreCase(name)) {
					return type;
				}
			}
			return null;
		}

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
	private String entityType;

	@Basic(optional = false)
	@Column(name = "entity_id")
	private Integer entityId;

	public ProgramFavorite() {
	}

	public ProgramFavorite(final String uniqueID, final FavoriteType favoriteType, final Integer entityId) {
		this.uniqueID = uniqueID;
		this.entityType = favoriteType.getName();
		this.entityId = entityId;
	}

	public Integer getProgramFavoriteId() {
		return this.programFavoriteId;
	}

	public void setProgramFavoriteId(final Integer programFavoriteId) {
		this.programFavoriteId = programFavoriteId;
	}

	public String getEntityType() {
		return this.entityType;
	}

	public void setEntityType(final String entityType) {
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
