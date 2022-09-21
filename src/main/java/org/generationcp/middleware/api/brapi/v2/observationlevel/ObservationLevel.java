package org.generationcp.middleware.api.brapi.v2.observationlevel;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObservationLevel {

	private String levelName;

	private Integer levelOrder;

	public ObservationLevel(final ObservationLevelEnum observationLevelEnum) {
		this.levelOrder = observationLevelEnum.getLevelOrder();
		this.levelName = observationLevelEnum.getLevelName();
	}

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

}
