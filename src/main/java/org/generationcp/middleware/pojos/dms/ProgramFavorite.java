
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

	public static final String ID_NAME = "programFavoriteId";

	public enum FavoriteType {
		LOCATION("LOCATION"), METHOD("METHODS"), VARIABLE("VARIABLES");

		private final String name;

		FavoriteType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static FavoriteType getByName(String name) {
			for (FavoriteType type : FavoriteType.values()) {
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

	public Integer getProgramFavoriteId() {
		return this.programFavoriteId;
	}

	public void setProgramFavoriteId(Integer programFavoriteId) {
		this.programFavoriteId = programFavoriteId;
	}

	public String getEntityType() {
		return this.entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Integer getEntityId() {
		return this.entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public String getUniqueID() {
		return this.uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

}
