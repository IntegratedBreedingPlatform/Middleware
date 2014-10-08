package org.generationcp.middleware.pojos.dms;

import javax.persistence.*;

@Entity
@Table(	name = "program_favorites")
public class ProgramFavorite {
	
	public enum FavoriteType {
		LOCATION("LOCATION"),
		METHOD("METHODS")
		;
		
		private final String name;
		
		private FavoriteType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
		public static FavoriteType getByName(String name) {
			for (FavoriteType type : values()) {
				if (type.getName().equalsIgnoreCase(name)) {
					return type;
				}
			}
			return null;
		}
		
	}

	@Id
	@Basic(optional = false)
	@Column(name = "id")
	private Integer programFavoriteId;
	
	@Basic(optional = false)
	@Column(name = "entity_type")
	private String entityType;
	
	@Basic(optional = false)
	@Column(name = "entity_id")
	private Integer entityId;

	public Integer getProgramFavoriteId() {
		return programFavoriteId;
	}

	public void setProgramFavoriteId(Integer programFavoriteId) {
		this.programFavoriteId = programFavoriteId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}
	
	
}
