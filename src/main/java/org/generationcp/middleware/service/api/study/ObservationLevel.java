package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObservationLevel {

	private String levelCode;

	private String levelName;

	private Integer levelOrder;

	public ObservationLevel(final Integer levelOrder, final String levelName) {
		this.levelOrder = levelOrder;
		this.levelName = levelName;
	}

	public String getLevelName() {
		return this.levelName;
	}

	public void setLevelName(final String levelName) {
		this.levelName = levelName;
	}

	public Integer getLevelOrder() {
		return this.levelOrder;
	}

	public void setLevelOrder(final Integer levelOrder) {
		this.levelOrder = levelOrder;
	}

	public String getLevelCode() {
		return this.levelCode;
	}

	public void setLevelCode(final String levelCode) {
		this.levelCode = levelCode;
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
